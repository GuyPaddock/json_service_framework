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

package com.rosieapp.services.common.model.identification;

import com.github.jasminb.jsonapi.ResourceIdHandler;
import java.util.Optional;

/**
 * A {@link ResourceIdHandler} that is used to marshall model identifiers into and out of JSON
 * documents.
 */
public class JsonModelIdHandler
implements ResourceIdHandler {
  @Override
  public String asString(final Object identifier) {
    return Optional.ofNullable(identifier).map(Object::toString).orElse(null);
  }

  @Override
  public Object fromString(final String identifier) {
    return ModelIdentifierFactory.valueOf(identifier);
  }
}
