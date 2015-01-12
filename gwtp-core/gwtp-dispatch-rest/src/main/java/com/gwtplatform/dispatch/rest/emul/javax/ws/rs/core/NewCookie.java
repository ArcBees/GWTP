/**
 * Copyright 2015 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package javax.ws.rs.core;

public class NewCookie extends Cookie {
    public static final int DEFAULT_MAX_AGE = -1;
    public static final boolean DEFAULT_SECURE = false;

    private final String comment;
    private final int maxAge;
    private final boolean secure;

    public NewCookie(
            String name,
            String value) {
        this(name, value, null, null, null, DEFAULT_MAX_AGE, false);
    }

    public NewCookie(
            String name,
            String value,
            String path,
            String domain,
            String comment,
            int maxAge,
            boolean secure) {
        this(name, value, path, domain, DEFAULT_VERSION, comment, maxAge, secure);
    }

    public NewCookie(
            String name,
            String value,
            String path,
            String domain,
            int version,
            String comment,
            int maxAge,
            boolean secure) {
        super(name, value, path, domain, version);

        this.comment = comment;
        this.maxAge = maxAge;
        this.secure = secure;
    }

    public NewCookie(Cookie cookie) {
        this(cookie, null, DEFAULT_MAX_AGE, DEFAULT_SECURE);
    }

    public NewCookie(
            Cookie cookie,
            String comment,
            int maxAge,
            boolean secure) {
        super(cookie == null ? null : cookie.getName(),
                cookie == null ? null : cookie.getValue(),
                cookie == null ? null : cookie.getPath(),
                cookie == null ? null : cookie.getDomain(),
                cookie == null ? DEFAULT_VERSION : cookie.getVersion());

        this.comment = comment;
        this.maxAge = maxAge;
        this.secure = secure;
    }

    public static NewCookie valueOf(String newCookie) throws IllegalArgumentException {
        if (newCookie == null) {
            throw new IllegalArgumentException("newCookie==null");
        }

        String cookieName = null;
        String cookieValue = null;
        String comment = null;
        String domain = null;
        int maxAge = NewCookie.DEFAULT_MAX_AGE;
        String path = null;
        boolean secure = false;
        int version = DEFAULT_VERSION;

        String parts[] = newCookie.split(COOKIE_PARTS_PATTERN);

        for (String part : parts) {
            String nameValue[] = part.split("=", 2);
            String partName = nameValue.length > 0 ? nameValue[0].trim() : "";
            String partValue = nameValue.length > 1 ? nameValue[1].trim() : "";

            if (partValue.startsWith("\"") && partValue.endsWith("\"") && partValue.length() > 1) {
                partValue = partValue.substring(1, partValue.length() - 1);
            }

            if (partName.startsWith("Comment")) {
                comment = partValue;
            } else if (partName.startsWith("Domain")) {
                domain = partValue;
            } else if (partName.startsWith("Max-Age")) {
                maxAge = Integer.parseInt(partValue);
            } else if (partName.startsWith("Path")) {
                path = partValue;
            } else if (partName.startsWith("Secure")) {
                secure = true;
            } else if (partName.startsWith("Version")) {
                version = Integer.parseInt(partValue);
            } else {
                cookieName = partName;
                cookieValue = partValue;
            }
        }

        return new NewCookie(cookieName, cookieValue, path, domain, version, comment, maxAge, secure);
    }

    public String getComment() {
        return comment;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public boolean isSecure() {
        return secure;
    }

    public Cookie toCookie() {
        return new Cookie(getName(), getValue(), getPath(), getDomain(), getVersion());
    }

    @Override
    public String toString() {
        return "NewCookie{" + super.toString() +
                ", comment='" + comment + '\'' +
                ", maxAge=" + maxAge +
                ", secure=" + secure +
                "}";
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 59 * hash + (comment != null ? comment.hashCode() : 0);
        hash = 59 * hash + maxAge;
        hash = 59 * hash + (secure ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        NewCookie other = (NewCookie) obj;
        return fieldsEquals(other)
                && comment != null && comment.equals(other.comment)
                && maxAge == other.maxAge
                && secure == other.secure;
    }
}
