package com.rosieapp.services.common.model.filtering;

import com.rosieapp.services.common.model.Model;

/**
 * Provides an interface for identifying if a given model matches a previously-established set of
 * criteria.
 * <p>
 * Model filters are obtained through {@link ModelFilterBuilder} interfaces, which themselves can
 * be obtained through the {@link com.rosieapp.services.common.model.construction.ModelBuilder}
 * interface used for constructing model instances. The same values used for assembling models are
 * typically used to construct filters that match them.
 * <p>
 * At present, this interface and the {@link FilterCriterion} interface are identical, but
 * conceptually criteria and model filters are distinct. Allowing one to extend the other also has
 * the added benefit of allowing filters to be composed as criteria within other filters.
 *
 * @param <M>
 *        The type of model that the filter applies to.
 */
public interface ModelFilter<M extends Model> extends FilterCriterion<M> {
}
