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

package com.rosieapp.services.common.request;

import com.github.jasminb.jsonapi.JSONAPIDocument;
import com.rosieapp.services.common.exception.RequestFailedException;
import com.rosieapp.services.common.model.Model;
import com.rosieapp.services.common.util.Requests;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A proxy object for a remote collection of models that are accessible through a paged resource.
 *
 * <p>This class transparently handles requesting each page of results behind an {@link Iterable}
 * interface, making it compatible with the Java streams API. Note, however, that iterating over
 * the full collection can incur a significant overhead from constantly making requests to the
 * remote system.
 *
 * @param <M>
 *        The type of model expected to be represented by data returned by the remote resource.
 */
public class PagedCollection<M extends Model>
implements Iterable<M> {
  /**
   * Sentinel value for a page limit that is unbounded (i.e. unlimited).
   */
  public static final int PAGE_LIMIT_UNLIMITED = -1;

  private static final Logger LOGGER = LoggerFactory.getLogger(PagedCollection.class);

  private final Function<Integer, Call<JSONAPIDocument<List<M>>>> requestFunction;
  private final int startingPageNumber;
  private final int pageLimit;

  /**
   * Constructor for {@code PagedCollection}.
   *
   * <p>Initializes the new instance to use the specified request function to obtain each page of
   * results, starting at page number 1, and requesting up to the specified maximum number of pages.
   *
   * <p>With care, {@link #PAGE_LIMIT_UNLIMITED} can be passed-in to request as many pages as the
   * remote resource will provide. This option should be used sparingly, as it can easily overwhelm
   * the remote resource with requests, possibly resulting in downtime or rate limiting.
   *
   * @param requestFunction
   *        The function to call with a page number in order to obtain each page of results.
   * @param pageLimit
   *        The maximum number of pages (starting from the {@code startingPageNumber}) to process.
   *
   */
  public PagedCollection(final Function<Integer, Call<JSONAPIDocument<List<M>>>> requestFunction,
                         final int pageLimit) {
    this(requestFunction, 1, pageLimit);
  }

  /**
   * Constructor for {@code PagedCollection}.
   *
   * <p>Initializes the new instance to use the specified request function to obtain each page of
   * results, starting at the specified page number, and requesting up to a maximum number of pages.
   *
   * @param requestFunction
   *        The function to call with a page number in order to obtain each page of results.
   * @param startingPageNumber
   *        The page number from which to start requesting data from the remote resource.
   * @param pageLimit
   *        The maximum number of pages (starting from the {@code startingPageNumber}) to process.
   */
  public PagedCollection(final Function<Integer, Call<JSONAPIDocument<List<M>>>> requestFunction,
                         final int startingPageNumber, final int pageLimit) {

    this.requestFunction    = requestFunction;
    this.startingPageNumber = startingPageNumber;
    this.pageLimit          = pageLimit;
  }

  @Override
  public java.util.Iterator<M> iterator() {
    return new Iterator();
  }

  /**
   * Gets a sequential stream over the elements of this paged collection.
   *
   * @return  A stream over this paged collection.
   */
  public Stream<M> stream() {
    return StreamSupport.stream(this.spliterator(), false);
  }


  /**
   * Indicates if there is a limit or not on the number of pages that may be processed from the
   * remote resource.
   *
   * @return  {@code true} if there is no limit to the number of pages that can be processed; or,
   *          {@code false} if there is a limit in place.
   */
  private boolean hasUnlimitedPageLimit() {
    return (this.pageLimit == PAGE_LIMIT_UNLIMITED);
  }

  /**
   * Requests a page of results from the endpoint.
   *
   * @param   pageNumber
   *          The page number to fetch. Must be {@code >= 1}.
   *
   * @return  A JSON API document containing the results of the request.
   *
   * @throws  IllegalArgumentException
   *          If {@code pageNumber} is {@code <= 0}.
   * @throws  RequestFailedException
   *          If the request to the remote server fails, or returns an empty response body.
   */
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  private JSONAPIDocument<List<M>> requestPage(final int pageNumber)
  throws IllegalArgumentException, RequestFailedException {
    final Response<JSONAPIDocument<List<M>>> response;
    final JSONAPIDocument<List<M>>           responseBody;

    if (pageNumber <= 0) {
      throw new IllegalArgumentException("page number must be greater than 0");
    }

    try {
      response = this.requestFunction.apply(pageNumber).execute();
    } catch (IOException ex) {
      throw new RequestFailedException(
        String.format("Failed to read page `%d` of results", pageNumber),
        ex);
    } catch (RuntimeException ex) { // Sadly, JSON-API converter throws this for bad input
      throw new RequestFailedException(
        String.format(
          "Received an improperly-formatted response when fetching page `%d` of results.",
          pageNumber),
        ex);
    }

    if (!response.isSuccessful()) {
      throw new RequestFailedException(
        Requests.failureResponseToString(
          String.format(
            "Server-side failure encountered when fetching page `%d` of results", pageNumber),
          response));
    }

    responseBody = response.body();

    if (responseBody == null) {
      throw new RequestFailedException(
        String.format(
          "Received an empty response when fetching page `%d` of results.", pageNumber));
    }

    return responseBody;
  }

  /**
   * A sequential iterator over the records of the paged collection resource.
   *
   * <p>Each iterator is independent, allowing multiple iterators to be used in order to support
   * concurrent operations over the same paged collection.
   */
  private class Iterator
  implements java.util.Iterator<M> {
    private static final int NO_PAGE_LOADED = 0;

    private int currentPageNumber;
    private java.util.Iterator<M> currentPageIterator;
    private boolean atEnd;

    /**
     * Default constructor for {@code Iterator}.
     */
    public Iterator() {
      this.currentPageNumber = NO_PAGE_LOADED;
    }

    @Override
    public boolean hasNext() {
      if (!this.atEnd
          && ((this.currentPageIterator == null) || !this.currentPageIterator.hasNext())) {
        this.safelyRequestNextPage();
      }

      return !this.atEnd;
    }

    @Override
    public M next() {
      if (!this.hasNext()) {
        throw new NoSuchElementException("There are no more models in the collection");
      }

      return this.currentPageIterator.next();
    }

    /**
     * Attempts to request the next page from the remote resource, failing silently if it fails.
     *
     * <p>If the request succeeds, the results of the request are automatically available for the
     * iterator to continue returning via {@link #next()}. If the resource returns no more results,
     * or the page limit has been reached, then this iterator will be marked finished, such that
     * future calls to {@link #hasNext()} on this instance will return {@link false}.
     *
     * <p>If the request fails, it is treated the same as if the remote resource had no additional
     * results, and the iterator be marked finished.
     */
    private void safelyRequestNextPage() {
      try {
        this.requestNextPage();
      } catch (RequestFailedException ex) {
        LOGGER.error("Failed to request page `{}` of results.", this.currentPageNumber, ex);

        // Don't try again
        this.markFinished();
      }
    }

    /**
     * Requests the next page of data, unless the iterator is at the page limit.
     *
     * <p>If the request succeeds, the results of the request are automatically available for the
     * iterator to continue returning via {@link #next()}. If the resource returns no more results,
     * or the page limit has been reached, then this iterator will be marked finished, such that
     * future calls to {@link #hasNext()} on this instance will return {@link false}.
     *
     * @throws  RequestFailedException
     *          If the request for data fails for any reason.
     */
    private void requestNextPage()
    throws RequestFailedException {
      final int nextPageNumber = this.getAndIncrementNextPageNumber();

      if (this.isAtPageLimit()) {
        this.markFinished();
      } else {
        final JSONAPIDocument<List<M>>  responseBody;
        final List<M>                   modelList;

        responseBody = PagedCollection.this.requestPage(nextPageNumber);
        modelList    = responseBody.get();

        if (modelList.isEmpty()) {
          this.markFinished();
        } else {
          this.currentPageIterator = modelList.iterator();
        }
      }
    }

    /**
     * Determines whether or not this iterator has reached the page limit for the collection, if
     * one is in place.
     *
     * @return  {@code true} if there is a page limit and it has been reached; or, {@code false} if
     *          there is either no page limit, or it has not yet been reached.
     */
    @SuppressWarnings("UnnecessaryLocalVariable")
    private boolean isAtPageLimit() {
      final boolean atPageLimit;

      if (PagedCollection.this.hasUnlimitedPageLimit()) {
        atPageLimit = false;
      } else {
        final PagedCollection collection    = PagedCollection.this;
        final int             startingPage  = collection.startingPageNumber,
                              limit         = collection.pageLimit,
                              currentPage   = this.currentPageNumber;

        atPageLimit = (currentPage >= (startingPage + limit));
      }

      return atPageLimit;
    }

    /**
     * Gets and increments the next page number that this iterator should request.
     *
     * <p>If the iterator currently has no page loaded, the next page number of this iterator will
     * be set to the starting page number of the collection.
     *
     * @return  The one-based page number that should be requested next.
     */
    private int getAndIncrementNextPageNumber() {
      if (this.currentPageNumber == NO_PAGE_LOADED) {
        this.currentPageNumber = PagedCollection.this.startingPageNumber;
      } else {
        ++this.currentPageNumber;
      }

      return this.currentPageNumber;
    }

    /**
     * Marks this iterator as having reached the end of the records that it is navigating.
     */
    private void markFinished() {
      this.atEnd               = true;
      this.currentPageIterator = null;
    }
  }
}
