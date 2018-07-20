/*
 * Copyright (c) 2018 Rosie Applications Inc. All rights reserved.
 */

package com.rosieapp.services.common.request;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.runner.RunWith;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

@RunWith(Spectrum.class)
@SuppressWarnings({
  "ClassInitializerMayBeStatic",
  "CodeBlock2Expr",
  "Convert2MethodRef"
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
        .withAuthToken("token")
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
