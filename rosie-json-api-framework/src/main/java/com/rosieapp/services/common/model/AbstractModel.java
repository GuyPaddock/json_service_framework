/*
 * Copyright (c) 2017-2018 Rosie Applications, Inc.
 */

package com.rosieapp.services.common.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.annotations.Id;
import com.rosieapp.services.common.model.identification.JsonModelIdHandler;
import com.rosieapp.services.common.model.identification.ModelIdentifier;
import com.rosieapp.services.common.model.identification.NewModelIdentifier;
import java.util.Objects;

/**
 * Optional, abstract parent class provided for use by all models in the system.
 *
 * <p>This implementation provides built-in handling for the identifier fields, which require
 * special handling for JSON API serialization and de-serialization.
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public abstract class AbstractModel
implements Model {
  /**
   * The identifier for this model.
   *
   * <p>This exists as a standalone field within the builder, rather than being stored in some other
   * structure (e.g. a map), to allow it to be annotated for JSON API Converter. This ensures that
   * identifiers for model sub-classes are already configured for JSON API serialization and
   * de-serialization.
   */
  @Id(value = JsonModelIdHandler.class)
  private ModelIdentifier id;

  /**
   * Constructor for {@code AbstractModel}.
   */
  public AbstractModel() {
    this.id = NewModelIdentifier.getInstance();
  }

  @Override
  public synchronized void assignId(final ModelIdentifier newId)
  throws IllegalStateException {
    final ModelIdentifier existingId;

    Objects.requireNonNull(newId, "`newId` cannot be null");

    existingId = this.getId();

    if (!newId.equals(existingId)) {
      if (!existingId.isObjectNew()) {
        throw new IllegalStateException(
          String.format(
            "This model already has an existing identifier set. An attempt was made to change the " +
            "identifier from `%s` to `%s`", existingId, newId));
      }

      this.id = newId;
    }
  }

  @Override
  public ModelIdentifier getId() {
    return this.id;
  }

  @Override
  public boolean isNew() {
    return (this.getId().isObjectNew());
  }

  /**
   * {@inheritDoc}
   *
   * <p>In this base implementation, two models are equal if and only if they are the same type and
   * have the same ID. If two different models have the same type, but both objects do not have an
   * assigned ID (i.e. they are new and un-persisted), they are not considered equal. This ensures
   * that multiple un-persisted instances with different data are not, by default, considered to be
   * equivalent simply as a consequence of not yet having been persisted.
   */
  @Override
  public boolean equals(Object other) {
    final boolean result;

    if (this == other) {
      result = true;
    }
    else if ((other == null) || (this.getClass() != other.getClass())) {
      result = false;
    }
    else {
      AbstractModel otherModel = (AbstractModel)other;

      // Don't consider two objects that lack an ID to be equal.
      result = Objects.equals(this.id, otherModel.id) && (!this.isNew());
    }

    return result;
  }

  /**
   * {@inheritDoc}
   *
   * <p>In this base implementation, a model's hashcode is based on its ID unless the model does not
   * have an assigned ID (i.e. it is new and un-persisted). For new models, the JDK's base
   * hashcode implementation is used.
   */
  @Override
  public int hashCode() {
    final int hashCode;

    if (this.id.isObjectNew()) {
      hashCode = super.hashCode();
    } else {
      hashCode = Objects.hash(id);
    }

    return hashCode;
  }

  /**
   * {@inheritDoc}
   *
   * <p>The identifier of the cloned object is automatically reset to a {@link NewModelIdentifier}
   * placeholder, so it is possible for the clone to be saved as a new, independent model instance.
   */
  @Override
  protected Object clone() throws CloneNotSupportedException {
    AbstractModel clone = (AbstractModel)super.clone();

    clone.id = NewModelIdentifier.getInstance();

    return clone;
  }
}
