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

package com.rosieapp.services.common.model.identification;

import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
@SuppressWarnings({
  "CodeBlock2Expr",
  "ClassInitializerMayBeStatic"
})
public class JsonModelIdHandlerTest {
  {
    describe("#asString", () -> {
      context("when the provided identifier is null", () -> {
        it("returns null", () -> {
          JsonModelIdHandler handler = new JsonModelIdHandler();
          String id = handler.asString(null);
          assertThat(id).isNull();
        });
      });

      context("when the provided identifier is not null", () -> {
        it("string representation of the identifier value", () -> {
          LongIdentifier id = new LongIdentifier(123);
          JsonModelIdHandler handler = new JsonModelIdHandler();

          assertThat(handler.asString(id)).isEqualTo(id.toString());
        });
      });
    });

    describe("#fromString", () -> {
      context("when the provided value is null", () -> {
        it("throws a NullPointerException", () -> {
          JsonModelIdHandler handler = new JsonModelIdHandler();

          assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> {
            handler.fromString(null);
          }).withMessage("identifier cannot be null");
        });
      });

      context("when the provided value is not null", () -> {
        it("returns a model identifier with the provided value", () -> {
          JsonModelIdHandler handler = new JsonModelIdHandler();
          ModelIdentifier id = (ModelIdentifier) handler.fromString("123");

          assertThat(id.toString()).isEqualTo("123");
        });
      });
    });
  }
}
