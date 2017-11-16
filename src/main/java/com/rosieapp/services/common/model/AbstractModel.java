package com.rosieapp.services.common.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.annotations.Id;
import com.rosieapp.services.common.model.identifiers.JsonModelIdHandler;
import com.rosieapp.services.common.model.identifiers.NewModelIdentifier;
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
  public ModelIdentifier getId() {
    return id;
  }

  @Override
  public boolean isNew() {
    return (this.getId().isObjectNew());
  }

  /**
   * Sets the identifier for this model.
   * <p>
   * The model must not already have an existing object identifier set, with the exception that the
   * model may have a {@link NewModelIdentifier}
   * set.
   *
   * @param   id
   *          The new ID for this object.
   *
   * @throws  IllegalArgumentException
   *          If this model already has an identifier set, and the identifier does not represent
   *          a new object identifier.
   */
  protected void setId(final ModelIdentifier id)
  throws IllegalArgumentException {
    final ModelIdentifier existingId = this.getId();

    if ((existingId != null) && !existingId.isObjectNew()) {
      throw new IllegalStateException("This model already has an existing identifier set.");
    }

    this.id = id;
  }
}
