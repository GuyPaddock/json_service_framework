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

import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
@SuppressWarnings({
  "ClassInitializerMayBeStatic",
  "CodeBlock2Expr"
})
public class NewModelIdentifierTest {
  {
    describe("#getInstance", () -> {
      it("always returns the same instance", () -> {
        NewModelIdentifier first = NewModelIdentifier.getInstance();
        NewModelIdentifier second = NewModelIdentifier.getInstance();

        assertThat(first).isSameAs(second);
      });
    });

    describe("#toString", () -> {
      it("returns null", () -> {
        String value = NewModelIdentifier.getInstance().toString();

        assertThat(value).isNull();
      });
    });

    describe("#isObjectNew", () -> {
      it("returns true", () -> {
        assertThat(NewModelIdentifier.getInstance().isObjectNew()).isTrue();
      });
    });
  }
}
