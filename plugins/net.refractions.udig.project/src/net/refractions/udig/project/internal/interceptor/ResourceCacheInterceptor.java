/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.internal.interceptor;

import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IResourceCachingInterceptor;

import org.geotools.styling.Style;
import org.opengis.coverage.grid.GridCoverage;

/**
 * Caches all resources and returns the cached instance.
 * <p>
 * This is used to ensure layers do not request a new feature source on every use.
 * 
 * @author Jesse
 * @since 1.1.0
 */
@SuppressWarnings("unchecked")
public class ResourceCacheInterceptor implements IResourceCachingInterceptor {
    public static final String ID = "net.refractions.udig.project.caching"; //$NON-NLS-1$
    private Map<Class, Object> resources = new HashMap<Class, Object>();

    private <T> void registerClasses( Class<T> clazz, Object obj ) {
        if (obj instanceof Style || obj instanceof GridCoverage) {
            return;
        }
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            registerClasses(clazz.getSuperclass(), obj);
        }
        for( int i = 0; i < clazz.getInterfaces().length; i++ ) {
            registerClasses(clazz.getInterfaces()[i], obj);
        }
        resources.put(clazz, obj);
    }
    public <T> T get( ILayer layer, Class<T> requestedType ) {
        return (T) resources.get(requestedType);
    }

    public <T> boolean isCached( ILayer layer, IGeoResource resource, Class<T> requestedType ) {
        // return resources.containsKey(resource.getClass());
        return resources.containsKey(requestedType);
    }

    public <T> void put( ILayer layer, T resource, Class<T> requestedType ) {
        if (resource != null) {
            registerClasses(resource.getClass(), resource);
        }
    }
}
