/*
 * Copyright 2024-2027 CIRPASS-2
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.extrared.dpp.renderer.business.search.filter;

/** Abstract implementation of a filter with a left and right parameter. */
public abstract class BiFilter implements Filter {

    private Filter left;

    private Filter right;

    protected BiFilter(Filter left, Filter right) {
        this.left = left;
        this.right = right;
    }

    public Filter getLeft() {
        return left;
    }

    public void setLeft(Filter left) {
        this.left = left;
    }

    public Filter getRight() {
        return right;
    }

    public void setRight(Filter right) {
        this.right = right;
    }

    public Literal getLiteral() {
        if (right instanceof Literal) return (Literal) right;
        if (left instanceof Literal) return (Literal) left;
        return null;
    }
}
