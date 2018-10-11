/*
 * Copyright (c) 2017-2018 Rosie Applications Inc.
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

package com.rosieapp.services.common.model.fieldhandling;

import java.lang.reflect.Field;

/**
 * A pre-processor that passes field values through, exactly as they are, without any processing at
 * all.
 *
 * <p>This pre-processor should only be used for values that are all right to be shared across
 * several all instances of the same model. For example, this can be used for immutable collections
 * or constant values for faster construction performance and better control over which object
 * instance ends up inside each model instance as values pass through the model's builder.
 */
public class PassthroughPreprocessor
implements FieldValuePreprocessor {
  @Override
  public <T> T preprocessField(final Field field, final T fieldValue) {
    return fieldValue;
  }
}
