package com.rosieapp.services.common.request;

import static com.greghaskins.spectrum.dsl.specification.Specification.afterEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.beforeAll;
import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.jasminb.jsonapi.annotations.Type;
import com.greghaskins.spectrum.Spectrum;
import com.rosieapp.services.common.client.ServiceClientBuilder;
import com.rosieapp.services.common.model.AbstractModel;
import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.identification.ModelIdentifier;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.runner.RunWith;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

@RunWith(Spectrum.class)
public class PagedCollectionTest {

  MockWebServer server;
  TestService testService;
  String testModelJson = " {\"data\":[{\"id\":123,"
      + "\"type\":\"test\",\"attributes\":{\"testVal\":\"abc123\"}}]}";

  {

    beforeEach(() -> setup());

    describe("#stream",() -> {
      context("when a paged collection is created without a starting page",() -> {
        it("starts at page 1",() -> {
          server.enqueue(new MockResponse().setBody("{ \"data\" : []}"));

          PagedCollection<TestModel> pagedCollection =
              new PagedCollection<>(
                  testService::getModels,
                  2
              );

          pagedCollection.stream().allMatch((_test) -> true);
          RecordedRequest request =  server.takeRequest();
          assertThat(request.getRequestUrl().toString()).contains("page[number]=1");
        });
      });
      context("when a paged collection is created with a starting page",() -> {
        it("starts requesting from the provided page number",() -> {
          server.enqueue(new MockResponse().setBody("{ \"data\" : []}}"));
          PagedCollection<TestModel> pagedCollection =
              new PagedCollection<>(
                  testService::getModels,
                  2,
                  3
              );
          pagedCollection.stream().allMatch((_test) -> true);
          RecordedRequest request =  server.takeRequest();
          assertThat(request.getRequestUrl().toString()).contains("page[number]=2");
        });
      });
      context("when the page limit has been reached",() -> {
        it("stops requesting additional pages",() -> {
          server.enqueue(new MockResponse().setBody(testModelJson));
          server.enqueue(new MockResponse().setBody(testModelJson));
          server.enqueue(new MockResponse().setBody(testModelJson));

          PagedCollection<TestModel> pagedCollection =
              new PagedCollection<>(
                  testService::getModels,
                  2
              );
          pagedCollection.stream().allMatch((_test) -> true);

          assertThat(server.getRequestCount()).isEqualTo(2);
        });
      });
      context("When an empty response is returned prior to the page limit",() -> {
        it("stops requesting additional pages",() -> {
          server.enqueue(new MockResponse().setBody("{ \"data\" : []}"));
          server.enqueue(new MockResponse().setBody(testModelJson));

          PagedCollection<TestModel> pagedCollection =
              new PagedCollection<>(
                  testService::getModels,
                  2
              );
          pagedCollection.stream().allMatch((_test) -> true);

          assertThat(server.getRequestCount()).isEqualTo(1);
        });
      });
      context("when an error response is returned prior to the page limit",() -> {
        it("stops requesting additional pages",() -> {
          server.enqueue(new MockResponse().setBody(testModelJson));
          server.enqueue(new MockResponse().setResponseCode(418));

          PagedCollection<TestModel> pagedCollection =
              new PagedCollection<>(
                  testService::getModels,
                  3
              );
          pagedCollection.stream().allMatch((_test) -> true);

          assertThat(server.getRequestCount()).isEqualTo(2);
        });
      });
      context("When page limit is unlimited",() -> {
        it("continues requesting pages until an empty response is returned",() -> {
          server.enqueue(new MockResponse().setBody(testModelJson));
          server.enqueue(new MockResponse().setBody(testModelJson));
          server.enqueue(new MockResponse().setBody(testModelJson));
          server.enqueue(new MockResponse().setBody("{ \"data\" : []}"));

          PagedCollection<TestModel> pagedCollection =
              new PagedCollection<>(
                  testService::getModels,
                  PagedCollection.PAGE_LIMIT_UNLIMITED
              );
          pagedCollection.stream().allMatch((_test) -> true);

          assertThat(server.getRequestCount()).isEqualTo(4);

        });
      });
    });
  }

  public void setup() throws IOException {
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

      public Builder() { super(TestService.class); }

      @Override
      protected List<Class<? extends Model>> getModelTypes() {
        return Arrays.asList(TestModel.class);
      }
    }
  }

  @Type("test")
  public static class TestModel extends AbstractModel {

    public TestModel(){}

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
