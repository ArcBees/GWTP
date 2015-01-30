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

public class Cookie {
    public static final int DEFAULT_VERSION = 1;

    protected static final String COOKIE_PARTS_PATTERN = "[;,]";

    private final String name;
    private final String value;
    private final int version;
    private final String path;
    private final String domain;

    public Cookie(
            String name,
            String value,
            String path,
            String domain,
            int version) {
        if (name == null) {
            throw new IllegalArgumentException("name==null");
        }

        this.name = name;
        this.value = value;
        this.version = version;
        this.domain = domain;
        this.path = path;
    }

    public Cookie(
            String name,
            String value,
            String path,
            String domain) {
        this(name, value, path, domain, DEFAULT_VERSION);
    }

    public Cookie(
            String name,
            String value) {
        this(name, value, null, null);
    }

    public static Cookie valueOf(String value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException("value==null");
        }
        try {
            int version = 0;
            String domain = null;
            String path = null;
            String cookieName = null;
            String cookieValue = null;

            String parts[] = value.split(COOKIE_PARTS_PATTERN);

            for (String part : parts) {
                String nameValue[] = part.split("=", 2);
                String name = nameValue.length > 0 ? nameValue[0].trim() : "";
                String partValue = nameValue.length > 1 ? nameValue[1].trim() : "";

                if (partValue.startsWith("\"") && partValue.endsWith("\"") && partValue.length() > 1) {
                    partValue = partValue.substring(1, partValue.length() - 1);
                }

                if (!name.startsWith("$")) {
                    cookieName = name;
                    cookieValue = partValue;
                } else if (name.startsWith("$Version")) {
                    version = Integer.parseInt(partValue);
                } else if (name.startsWith("$Path")) {
                    path = partValue;
                } else if (name.startsWith("$Domain")) {
                    domain = partValue;
                }
            }
            return new Cookie(cookieName, cookieValue, path, domain, version);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unable to parse cookie '" + value + "'", ex);
        }
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public int getVersion() {
        return version;
    }

    public String getDomain() {
        return domain;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "Cookie{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", version=" + version +
                ", path='" + path + '\'' +
                ", domain='" + domain + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (name != null ? name.hashCode() : 0);
        hash = 97 * hash + (value != null ? value.hashCode() : 0);
        hash = 97 * hash + version;
        hash = 97 * hash + (path != null ? path.hashCode() : 0);
        hash = 97 * hash + (domain != null ? domain.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
                && getClass() == obj.getClass()
                && fieldsEquals((Cookie) obj);
    }

    protected boolean fieldsEquals(Cookie other) {
        return name != null && name.equals(other.name)
                && value != null && value.equals(other.value)
                && version == other.version
                && path != null && path.equals(other.path)
                && domain != null && domain.equals(other.domain);
    }
}
