/*
 * Copyright (c) 2018 Rosie Applications Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.rosieapp.services.common.request;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.let;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.github.jasminb.jsonapi.annotations.Type;
import com.greghaskins.spectrum.Spectrum;
import com.rosieapp.services.common.client.ServiceClientBuilder;
import com.rosieapp.services.common.model.AbstractModel;
import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.identification.ModelIdentifier;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.runner.RunWith;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

@RunWith(Spectrum.class)
@SuppressWarnings({
  "ClassInitializerMayBeStatic",
  "CodeBlock2Expr",
  "Convert2MethodRef",
  "Duplicates"
})
public class PagedCollectionTest {
  MockWebServer server;
  TestService testService;

  final String testModelJson =
    "{"
    + "\"data\":[{"
      + "\"id\":123,"
      + "\"type\":\"test\","
      + "\"attributes\":{"
        + "\"testVal\":\"abc123\""
      + "}"
    + "}]}";

  {
    beforeEach(() -> setupMockServer());

    describe("#stream", () -> {
      context("when a paged collection is created without a starting page", () -> {
        it("starts at page 1", () -> {
          this.server.enqueue(new MockResponse().setBody("{ \"data\" : []}"));

          PagedCollection<TestModel> pagedCollection =
            new PagedCollection<>(
              this.testService::getModels,
              2
            );

          //noinspection ResultOfMethodCallIgnored
          pagedCollection.stream().allMatch((_test) -> true);

          RecordedRequest request = this.server.takeRequest();
          assertThat(request.getRequestUrl().toString()).contains("page[number]=1");
        });
      });

      context("when a paged collection is created with a starting page", () -> {
        it("starts requesting from the provided page number", () -> {
          this.server.enqueue(new MockResponse().setBody("{ \"data\" : []}}"));

          PagedCollection<TestModel> pagedCollection =
            new PagedCollection<>(
              this.testService::getModels,
              2,
              3
            );

          //noinspection ResultOfMethodCallIgnored
          pagedCollection.stream().allMatch((_test) -> true);

          RecordedRequest request = this.server.takeRequest();
          assertThat(request.getRequestUrl().toString()).contains("page[number]=2");
        });
      });

      context("when the page limit has been reached", () -> {
        it("stops requesting additional pages", () -> {
          this.enqueueResponseMultipleTimes(3, this.testModelJson);

          PagedCollection<TestModel> pagedCollection =
            new PagedCollection<>(
              this.testService::getModels,
              2
            );

          //noinspection ResultOfMethodCallIgnored
          pagedCollection.stream().allMatch((_test) -> true);

          assertThat(this.server.getRequestCount()).isEqualTo(2);
        });
      });

      context("when an empty response is returned prior to the page limit", () -> {
        it("stops requesting additional pages", () -> {
          this.server.enqueue(new MockResponse().setBody("{ \"data\" : []}"));
          this.server.enqueue(new MockResponse().setBody(this.testModelJson));

          PagedCollection<TestModel> pagedCollection =
            new PagedCollection<>(
              this.testService::getModels,
              2
            );

          //noinspection ResultOfMethodCallIgnored
          pagedCollection.stream().allMatch((_test) -> true);

          assertThat(this.server.getRequestCount()).isEqualTo(1);
        });
      });

      context("when an error response is returned prior to the page limit", () -> {
        it("stops requesting additional pages", () -> {
          this.server.enqueue(new MockResponse().setBody(this.testModelJson));
          this.server.enqueue(new MockResponse().setResponseCode(418));

          PagedCollection<TestModel> pagedCollection =
            new PagedCollection<>(
              this.testService::getModels,
              3
            );

          //noinspection ResultOfMethodCallIgnored
          pagedCollection.stream().allMatch((_test) -> true);

          assertThat(this.server.getRequestCount()).isEqualTo(2);
        });
      });

      context("when page limit is unlimited", () -> {
        it("continues requesting pages until an empty response is returned", () -> {
          this.enqueueResponseMultipleTimes(3, this.testModelJson);
          this.server.enqueue(new MockResponse().setBody("{ \"data\" : []}"));

          PagedCollection<TestModel> pagedCollection =
            new PagedCollection<>(
              this.testService::getModels,
              PagedCollection.PAGE_LIMIT_UNLIMITED
            );

          //noinspection ResultOfMethodCallIgnored
          pagedCollection.stream().allMatch((_test) -> true);

          assertThat(this.server.getRequestCount()).isEqualTo(4);
        });
      });

      context("when a page number less than 1 is requested", () -> {
        it("throws an IllegalArgumentException", () -> {
          this.server.enqueue(new MockResponse().setBody("{ \"data\" : []}"));

          assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            PagedCollection<TestModel> pagedCollection =
              new PagedCollection<>(
                this.testService::getModels,
                0,
                PagedCollection.PAGE_LIMIT_UNLIMITED
              );

            //noinspection ResultOfMethodCallIgnored
            pagedCollection.stream().allMatch((_test) -> true);
          });
        });
      });
    });

    context("when the request function throws an IOException", () -> {
      final Supplier<Call<JSONAPIDocument<List<TestModel>>>> mockCall =
        let(() -> {
          @SuppressWarnings("unchecked") final Call<JSONAPIDocument<List<TestModel>>> mock =
            mock(Call.class);

          when(mock.execute()).thenThrow(new IOException("Terrible badness!"));

          return mock;
        });

      it("stops requesting additional pages", () -> {
        // CHECKSTYLE IGNORE Indentation FOR NEXT 6 LINES
        PagedCollection<TestModel> pagedCollection =
          new PagedCollection<>(
            (page) -> mockCall.get(),
            1,
            PagedCollection.PAGE_LIMIT_UNLIMITED
          );

        //noinspection ResultOfMethodCallIgnored
        pagedCollection.stream().allMatch((_test) -> true);

        assertThat(this.server.getRequestCount()).isEqualTo(0);
        verify(mockCall.get(), times(1)).execute();
      });
    });

    context("when an error response is returned", () -> {
      it("stops requesting additional pages", () -> {
        this.server.enqueue(new MockResponse().setResponseCode(418));

        PagedCollection<TestModel> pagedCollection =
          new PagedCollection<>(
            this.testService::getModels,
            1,
            PagedCollection.PAGE_LIMIT_UNLIMITED
          );

        //noinspection ResultOfMethodCallIgnored
        pagedCollection.stream().allMatch((_test) -> true);

        assertThat(this.server.getRequestCount()).isEqualTo(1);
      });
    });

    context("when a malformed response is returned", () -> {
      it("stops requesting additional pages", () -> {
        this.server.enqueue(new MockResponse().setBody(""));

        PagedCollection<TestModel> pagedCollection =
          new PagedCollection<>(
            this.testService::getModels,
            1,
            PagedCollection.PAGE_LIMIT_UNLIMITED
          );

        //noinspection ResultOfMethodCallIgnored
        pagedCollection.stream().allMatch((_test) -> true);

        assertThat(this.server.getRequestCount()).isEqualTo(1);
      });
    });

    context("when a blank response is returned", () -> {
      @SuppressWarnings("unchecked")
      final Supplier<Response<JSONAPIDocument<List<TestModel>>>> badResponse =
        let(() -> Response.success(null));

      final Supplier<Call<JSONAPIDocument<List<TestModel>>>> mockCall =
        let(() -> {
          @SuppressWarnings("unchecked")
          final Call<JSONAPIDocument<List<TestModel>>> mock = mock(Call.class);

          when(mock.execute()).thenReturn(badResponse.get());

          return mock;
        });

      it("stops requesting additional pages", () -> {
        // CHECKSTYLE IGNORE Indentation FOR NEXT 6 LINES
        PagedCollection<TestModel> pagedCollection =
          new PagedCollection<>(
            (page) -> mockCall.get(),
            1,
            PagedCollection.PAGE_LIMIT_UNLIMITED
          );

        //noinspection ResultOfMethodCallIgnored
        pagedCollection.stream().allMatch((_test) -> true);

        assertThat(this.server.getRequestCount()).isEqualTo(0);
        verify(mockCall.get(), times(1)).execute();
      });
    });
  }

  @SuppressWarnings("SameParameterValue")
  protected void enqueueResponseMultipleTimes(final int multiple, final String responseBody) {
    for (int count = 0; count < multiple; ++count) {
      this.server.enqueue(new MockResponse().setBody(responseBody));
    }
  }

  protected void setupMockServer() throws IOException {
    this.server = new MockWebServer();
    this.server.start();

    this.testService = new TestService.Builder()
        .withBaseUrl(this.server.url("/").toString())
        .forNormalRequests()
        .build();
  }

  interface TestService {
    @GET("test")
    Call<JSONAPIDocument<List<TestModel>>> getModels(@Query("page[number]") int pageNumber);

    class Builder extends ServiceClientBuilder<TestService> {

      public Builder() {
        super(TestService.class);
      }

      @Override
      protected List<Class<? extends Model>> getModelTypes() {
        return Collections.singletonList(TestModel.class);
      }
    }
  }

  @Type("test")
  public static class TestModel extends AbstractModel {
    public TestModel() {
      super();
    }

    @Override
    public void assignId(ModelIdentifier id) {
      super.assignId(id);
    }

    @Override
    public ModelIdentifier getId() {
      return super.getId();
    }

    @Override
    public boolean isNew() {
      return false;
    }
  }
}
