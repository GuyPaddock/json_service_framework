package com.rosieapp.services.common.util;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.let;
import static com.greghaskins.spectrum.dsl.specification.Specification.beforeAll;
import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import com.greghaskins.spectrum.Spectrum;
import java.util.function.Supplier;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.ResponseBody;
import org.junit.runner.RunWith;

import retrofit2.Response;

@RunWith(Spectrum.class)
public class RequestsTest {
  {
    describe(".failureResponseToString", () -> {
      context("when given a response that was successful", () -> {
        final Response<String> response = Response.success("Response body");
        it("throws an IllegalArgumentException", () -> {
          assertThatExceptionOfType(IllegalArgumentException.class)
              .isThrownBy(() -> {
                Requests.failureResponseToString("context message", response);
              })
              .withMessage(
                  "Response did not fail -- it was successful. This method must not be called for " +
                      "successful responses.")
              .withNoCause();
        });
      });

      context("when given an error response that has a message and an error body", () -> {
        final Response<String> response = Response.error(400, ResponseBody.create(MediaType.parse("application/json"),"error_body_string"));
        it("returns a string describing the failure", () -> {
          assertThat(Requests.failureResponseToString("context_message",response))
              .isEqualTo("context_message: 400 - Response.error() error_body_string");
        });
      });

      context("when given an error response that has no message but has an error body", () -> {
        final Response<String> response = Response.error(ResponseBody.create(MediaType.parse("application/json"),"error_body_string"),
            (new okhttp3.Response.Builder()).code(400)
                .message("")
                .protocol(Protocol.HTTP_1_1)
                .request((new okhttp3.Request.Builder())
                    .url("http://localhost/").build())
                .build());
        it("returns a string describing the failure without a message", () -> {
          assertThat(Requests.failureResponseToString("context_message",response))
              .isEqualTo("context_message: 400 error_body_string");
        });
      });

      context("when given an error response that has a message but an empty error body", () -> {
        final Response<String> response = Response.error(400, ResponseBody.create(MediaType.parse("application/json"),""));
        it("returns a string describing the failure without an error body", () -> {
          assertThat(Requests.failureResponseToString("context_message",response))
              .isEqualTo("context_message: 400 - Response.error()");
        });
      });

    });
  }
}
