/*
 * Licensed to ElasticSearch and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. ElasticSearch licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.index.query;

import org.apache.lucene.search.BooleanClause;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A filter that matches documents matching boolean combinations of other filters.
 *
 *
 */
public class BoolFilterBuilder extends BaseFilterBuilder {

    private ArrayList<Clause> clauses = new ArrayList<Clause>();

    private Boolean cache;
    private String cacheKey;

    private String filterName;

    /**
     * Adds a filter that <b>must</b> appear in the matching documents.
     */
    public BoolFilterBuilder must(FilterBuilder filterBuilder) {
        clauses.add(new Clause(filterBuilder, BooleanClause.Occur.MUST));
        return this;
    }

    /**
     * Adds a filter that <b>must not</b> appear in the matching documents.
     */
    public BoolFilterBuilder mustNot(FilterBuilder filterBuilder) {
        clauses.add(new Clause(filterBuilder, BooleanClause.Occur.MUST_NOT));
        return this;
    }

    /**
     * Adds multiple <i>should</i> filters.
     */
    public BoolFilterBuilder should(FilterBuilder... filterBuilders) {
        for (FilterBuilder filterBuilder : filterBuilders) {
            clauses.add(new Clause(filterBuilder, BooleanClause.Occur.SHOULD));
        }
        return this;
    }

    /**
     * Adds multiple <i>must</i> filters.
     */
    public BoolFilterBuilder must(FilterBuilder... filterBuilders) {
        for (FilterBuilder filterBuilder : filterBuilders) {
            clauses.add(new Clause(filterBuilder, BooleanClause.Occur.MUST));
        }
        return this;
    }

    /**
     * Adds multiple <i>must not</i> filters.
     */
    public BoolFilterBuilder mustNot(FilterBuilder... filterBuilders) {
        for (FilterBuilder filterBuilder : filterBuilders) {
            clauses.add(new Clause(filterBuilder, BooleanClause.Occur.MUST_NOT));
        }
        return this;
    }

    /**
     * Adds a filter that <i>should</i> appear in the matching documents. For a boolean filter
     * with no <tt>MUST</tt> clauses one or more <code>SHOULD</code> clauses must match a document
     * for the BooleanQuery to match.
     */
    public BoolFilterBuilder should(FilterBuilder filterBuilder) {
        clauses.add(new Clause(filterBuilder, BooleanClause.Occur.SHOULD));
        return this;
    }

    /**
     * Sets the filter name for the filter that can be used when searching for matched_filters per hit.
     */
    public BoolFilterBuilder filterName(String filterName) {
        this.filterName = filterName;
        return this;
    }

    /**
     * Should the filter be cached or not. Defaults to <tt>false</tt>.
     */
    public BoolFilterBuilder cache(boolean cache) {
        this.cache = cache;
        return this;
    }

    public BoolFilterBuilder cacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
        return this;
    }

    @Override
    protected void doXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject("bool");
        for (Clause clause : clauses) {
            if (clause.occur == BooleanClause.Occur.MUST) {
                builder.field("must");
                clause.filterBuilder.toXContent(builder, params);
            } else if (clause.occur == BooleanClause.Occur.MUST_NOT) {
                builder.field("must_not");
                clause.filterBuilder.toXContent(builder, params);
            } else if (clause.occur == BooleanClause.Occur.SHOULD) {
                builder.field("should");
                clause.filterBuilder.toXContent(builder, params);
            }
        }
        if (filterName != null) {
            builder.field("_name", filterName);
        }
        if (cache != null) {
            builder.field("_cache", cache);
        }
        if (cacheKey != null) {
            builder.field("_cache_key", cacheKey);
        }
        builder.endObject();
    }

    private static class Clause {
        final FilterBuilder filterBuilder;
        final BooleanClause.Occur occur;

        private Clause(FilterBuilder filterBuilder, BooleanClause.Occur occur) {
            this.filterBuilder = filterBuilder;
            this.occur = occur;
        }
    }
}