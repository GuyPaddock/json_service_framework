package com.rosieapp.services.common.model.construction;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.model.fieldhandling.FieldValueProvider;
import com.rosieapp.services.common.model.fieldhandling.StrictFieldProvider;
import com.rosieapp.services.common.model.identification.ModelIdentifier;
import com.rosieapp.services.common.model.identification.ModelIdentifierFactory;
import com.rosieapp.services.common.model.identification.NewModelIdentifier;
import java.util.Optional;

/**
 * Optional, abstract parent class provided for use by all model builders in the system.
 *
 * <p>This implementation provides built-in handling for the identifier fields, which require
 * special handling for JSON API serialization and de-serialization.
 *
 * @param <M>
 *        The type of model that the builder builds.
 * @param <B>
 *        The builder class itself. (This must be the same type as the class being defined, to avoid
 *        a {@code ClassCastException}).
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public abstract class AbstractModelBuilder<M extends Model, B extends AbstractModelBuilder<M, B>>
implements ModelBuilder<M> {
  private final FieldValueProvider valueProvider;

  private ModelIdentifier id;

  /**
   * Default constructor for {@link AbstractModelBuilder}.
   *
   * <p>Initializes the model builder to strictly validate required fields.
   */
  protected AbstractModelBuilder() {
    this(new StrictFieldProvider());
  }

  /**
   * Constructor for {@link AbstractModelBuilder}.
   *
   * @param valueProvider
   *        A provider for controlling how optional and required fields are handled during object
   *        construction.
   */
  protected AbstractModelBuilder(final FieldValueProvider valueProvider) {
    this.valueProvider = valueProvider;
  }

  /**
   * Builder method for constructing a model with an identifier derived by parsing a String value.
   *
   * <p>The String must represent either a UUID or a long integer value.
   *
   * @param   identifier
   *          The string from an identifier will be parsed.
   *          This must not be {@code null} and must represent a valid identifier.
   *
   * @return  This object, for chaining builder calls.
   *
   * @throws  IllegalArgumentException
   *          If {@code identifier} does not represent a valid identifier.
   * @throws  NullPointerException
   *          If {@code identifier} is {@code null}.
   */
  public B withId(final String identifier)
  throws IllegalArgumentException, NullPointerException {
    return this.withId(ModelIdentifierFactory.valueOf(identifier));
  }

  /**
   * Builder method for constructing a model with the specified identifier.
   *
   * @param   id
   *          The string from an identifier will be parsed.
   *
   * @return  This object, for chaining builder calls.
   */
  @SuppressWarnings("unchecked")
  public B withId(final ModelIdentifier id) {
    this.setId(id);

    return (B)this;
  }

  /**
   * Sets the identifier that will be used for new models being built.
   *
   * @param id
   *        The identifier to use for new models.
   */
  protected final void setId(final ModelIdentifier id) {
    this.id = id;
  }

  /**
   * Gets the identifier that is currently being used for new models being built.
   *
   * @return  The identifier being used for new models.
   */
  protected final ModelIdentifier getId() {
    return this.id;
  }

  /**
   * Builds a null-safe identifier for the model.
   *
   * <p>If an identifier was not provided via {@link #withId(ModelIdentifier)} or its permutations,
   * this method will automatically produce a {@link NewModelIdentifier} instead, to ensure that
   * locally-produced objects never have a {@code null} identifier.
   *
   * @return  An identifier to use for the new model instance.
   */
  protected ModelIdentifier buildId() {
    return Optional.ofNullable(this.getId()).orElse(NewModelIdentifier.getInstance());
  }

  /**
   * Requests, optionally validates, and then returns the value to use when populating the specified
   * required field for a model being constructed by this builder.
   *
   * <p>The request is delegated to the field value provider. If this builder has not been provided
   * with a value for the field (i.e. {@code fieldValue} is {@code null}), the field provider may
   * choose to communicate this by raising an {@link IllegalStateException}, or it may simply supply
   * {@code null} (or a different value of its own choosing) in place of the missing value.
   *
   * @see     FieldValueProvider
   *
   * @param   fieldValue
   *          The current value this builder has for the field.
   * @param   fieldName
   *          The name of the field. This may be used by the field value provider to construct an
   *          exception message if the field has no value.
   *
   * @param   <F>
   *          The type of value expected for the field.
   *
   * @return  Depending on the field value provider, this will typically be a non-null value to use
   *          for the field, but may be {@code null} if the value provider is lax on validating
   *          that all required fields are populated.
   *
   * @throws  IllegalStateException
   *          If the required field value is {@code null} or invalid, and the field value provider
   *          considers this to be an error.
   */
  protected <F> F getRequiredField(final F fieldValue, final String fieldName)
  throws IllegalStateException {
    return this.getValueProvider().getRequiredField(fieldValue, fieldName);
  }

  /**
   * Returns the value to use when populating the specified optional field for a model being
   * constructed by this builder.
   *
   * <p>The request is delegated to the field value provider. If this builder has not been provided
   * with a value for the field (i.e. {@code fieldValue} is {@code null}), then in place of the
   * missing value, the field provider may choose to return the {@code defaultValue} that the
   * builder has requested, or any other value of the provider's choosing.
   *
   * @see     FieldValueProvider
   *
   * @param   fieldValue
   *          The current value this builder has for the field.
   * @param   defaultValue
   *          The default value that the builder would prefer to receive if the {@code fieldValue}
   *          is {@code null}.
   *
   * @param   <F>
   *          The type of value expected for the field.
   *
   * @return  Depending on the field value provider, this will typically be either the value of
   *          the requested field, or the default value if the field did not have a value.
   */
  protected <F> F getOptionalField(final F fieldValue, final F defaultValue) {
    return this.getValueProvider().getOptionalField(fieldValue, defaultValue);
  }

  /**
   * Gets the provider (i.e. strategy) that dictates how the values of fields are retrieved and
   * validated.
   *
   * @return  The field value provider currently in use by this builder.
   */
  private FieldValueProvider getValueProvider() {
    return this.valueProvider;
  }
}
