package com.rosieapp.services.common.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.annotations.Id;
import com.rosieapp.services.common.model.identifiers.JsonModelIdHandler;
import com.rosieapp.services.common.model.identifiers.ModelIdentifier;

/**
 * Optional, abstract parent class provided for use by all models in the system.
 * <p>
 * This implementation provides built-in handling for the identifier fields, which require special
 * handling for JSON API serialization and de-serialization.
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public abstract class AbstractModel
implements Model {
  /**
   * The identifier for this model.
   * <p>
   * This exists as a standalone field within the builder, rather than being stored in some other
   * structure (e.g. a map), to allow it to be annotated for JSON API Converter. This ensures that
   * identifiers for model sub-classes are already configured for JSON API serialization and
   * de-serialization.
   */
  @Id(value = JsonModelIdHandler.class)
  private ModelIdentifier id;

  @Override
  public synchronized void assignId(final ModelIdentifier newId)
  throws IllegalArgumentException {
    final ModelIdentifier existingId = this.getId();

    if ((existingId != null) && !existingId.isObjectNew()) {
      throw new IllegalStateException(
        String.format(
          "This model already has an existing identifier set. An attempt was made to change the " +
          "identifier from `%s` to `%s`", existingId, newId));
    }

    this.id = newId;
  }

  @Override
  public ModelIdentifier getId() {
    return id;
  }

  @Override
  public boolean isNew() {
    return (this.getId().isObjectNew());
  }
}
