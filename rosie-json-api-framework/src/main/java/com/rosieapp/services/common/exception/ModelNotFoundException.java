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

package com.rosieapp.services.common.exception;

import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.identification.ModelIdentifier;
import java.text.MessageFormat;

/**
 * Exception thrown when a record of a specific type with a specific identifier was not found.
 */
public class ModelNotFoundException
extends Exception {
  /**
   * Initializes the exception to indicate that a model with the specified type and ID was not
   * found.
   *
   * @param modelType
   *        The type of model being located.
   * @param modelId
   *        The unique identifier for the target model.
   */
  public ModelNotFoundException(final Class<? extends Model> modelType,
                                final ModelIdentifier modelId) {
    this(modelType.getSimpleName(), modelId, null);
  }

  /**
   * Initializes the exception to indicate that a model with the specified type and ID was not
   * found.
   *
   * @param modelType
   *        The type of model being located.
   * @param modelId
   *        The unique identifier for the target model.
   * @param cause
   *        Original cause of exception
   */
  public ModelNotFoundException(final Class<? extends Model> modelType,
      final ModelIdentifier modelId, final Throwable cause) {
    this(modelType.getSimpleName(), modelId, cause);
  }

  /**
   * Initializes the exception to indicate that a model with the specified type and ID was not
   * found.
   *
   * @param modelType
   *        The type of model being located.
   * @param modelId
   *        The unique identifier for the target model.
   *
   */
  public ModelNotFoundException(final String modelType, final ModelIdentifier modelId) {
    this(modelType, modelId, null);
  }


  /**
   * Initializes the exception to indicate that a model with the specified type and ID was not
   * found.
   *
   * @param modelType
   *        The type of model being located.
   * @param modelId
   *        The unique identifier for the target model.
   * @param cause
   *        Original cause of exception
   */
  public ModelNotFoundException(final String modelType,
                                final ModelIdentifier modelId,
                                final Throwable cause) {
    super(
      MessageFormat.format(
          "No {0} that has an ID of `{1}` could be found", modelType, modelId), cause);
  }


  /**
   * Initializes the exception to indicate that a sub-model of the model with the specified type and
   * ID was not found.
   *
   * @param subModelType
   *        The type of model under the parent model that was expected, but not found.
   * @param parentModelType
   *        The type of model being located.
   * @param modelId
   *        The unique identifier for the target model.
   */
  public ModelNotFoundException(final Class<? extends Model> subModelType,
                                final Class<? extends Model> parentModelType,
                                final ModelIdentifier modelId) {
    this(subModelType.getSimpleName(), parentModelType.getSimpleName(), modelId);
  }

  /**
   * Initializes the exception to indicate that a sub-model of the model with the specified type and
   * ID was not found.
   *
   * @param subModelType
   *        The type of model under the parent model that was expected, but not found.
   * @param parentModelType
   *        The type of model being located.
   * @param modelId
   *        The unique identifier for the target model.
   */
  public ModelNotFoundException(final String subModelType, final String parentModelType,
                                final ModelIdentifier modelId) {
    super(
      MessageFormat.format(
        "`{0}` {1} has no `{0}` models", parentModelType, modelId, subModelType));
  }
}
