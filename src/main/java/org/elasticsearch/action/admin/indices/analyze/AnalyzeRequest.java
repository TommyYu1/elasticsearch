/*
 * Licensed to Elastic Search and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Elastic Search licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.action.admin.indices.analyze;

import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.support.single.custom.SingleCustomOperationRequest;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;

import java.io.IOException;

import static org.elasticsearch.action.ValidateActions.addValidationError;

/**
 * A request to analyze a text associated with a specific index. Allow to provide
 * the actual analyzer name to perform the analysis with.
 */
public class AnalyzeRequest extends SingleCustomOperationRequest<AnalyzeRequest> {

    private String index;

    private String text;

    private String analyzer;

    private String tokenizer;

    private String[] tokenFilters;

    private String field;

    AnalyzeRequest() {

    }

    /**
     * Constructs a new analyzer request for the provided text.
     *
     * @param text The text to analyze
     */
    public AnalyzeRequest(String text) {
        this.text = text;
    }

    /**
     * Constructs a new analyzer request for the provided index and text.
     *
     * @param index The index name
     * @param text  The text to analyze
     */
    public AnalyzeRequest(@Nullable String index, String text) {
        this.index = index;
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public AnalyzeRequest setIndex(String index) {
        this.index = index;
        return this;
    }

    public String getIndex() {
        return this.index;
    }

    public AnalyzeRequest setAnalyzer(String analyzer) {
        this.analyzer = analyzer;
        return this;
    }

    public String getAnalyzer() {
        return this.analyzer;
    }

    public AnalyzeRequest setTokenizer(String tokenizer) {
        this.tokenizer = tokenizer;
        return this;
    }

    public String getTokenizer() {
        return this.tokenizer;
    }

    public AnalyzeRequest setTokenFilters(String... tokenFilters) {
        this.tokenFilters = tokenFilters;
        return this;
    }

    public String[] getTokenFilters() {
        return this.tokenFilters;
    }

    public AnalyzeRequest setField(String field) {
        this.field = field;
        return this;
    }

    public String getField() {
        return this.field;
    }

    @Override
    public ActionRequestValidationException validate() {
        ActionRequestValidationException validationException = super.validate();
        if (text == null) {
            validationException = addValidationError("text is missing", validationException);
        }
        return validationException;
    }

    @Override
    public void readFrom(StreamInput in) throws IOException {
        super.readFrom(in);
        index = in.readOptionalString();
        text = in.readString();
        analyzer = in.readOptionalString();
        tokenizer = in.readOptionalString();
        int size = in.readVInt();
        if (size > 0) {
            tokenFilters = new String[size];
            for (int i = 0; i < size; i++) {
                tokenFilters[i] = in.readString();
            }
        }
        field = in.readOptionalString();
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeOptionalString(index);
        out.writeString(text);
        out.writeOptionalString(analyzer);
        out.writeOptionalString(tokenizer);
        if (tokenFilters == null) {
            out.writeVInt(0);
        } else {
            out.writeVInt(tokenFilters.length);
            for (String tokenFilter : tokenFilters) {
                out.writeString(tokenFilter);
            }
        }
        out.writeOptionalString(field);
    }
}
