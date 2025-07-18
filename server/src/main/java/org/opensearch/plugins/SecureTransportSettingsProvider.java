/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.plugins;

import org.opensearch.common.annotation.ExperimentalApi;
import org.opensearch.common.settings.Settings;
import org.opensearch.transport.Transport;
import org.opensearch.transport.TransportAdapterProvider;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * A provider for security related settings for transports.
 *
 * @opensearch.experimental
 */
@ExperimentalApi
public interface SecureTransportSettingsProvider {
    /**
     * Collection of additional {@link TransportAdapterProvider}s that are specific to particular transport
     * @param settings settings
     * @return a collection of additional {@link TransportAdapterProvider}s
     */
    default Collection<TransportAdapterProvider<Transport>> getTransportAdapterProviders(Settings settings) {
        return Collections.emptyList();
    }

    /**
     * Returns parameters that can be dynamically provided by a plugin providing a {@link SecureTransportSettingsProvider}
     * implementation
     * @param settings settings
     * @return an instance of {@link SecureTransportParameters}
     */
    default Optional<SecureTransportParameters> parameters(Settings settings) {
        return Optional.of(new DefaultSecureTransportParameters(settings));
    }

    /**
     * Dynamic parameters that can be provided by the {@link SecureTransportSettingsProvider}
     */
    @ExperimentalApi
    interface SecureTransportParameters {
        /**
         * Enable / Disable dual model (if supported by transport)
         * @return dual model enabled or not
         */
        boolean dualModeEnabled();

        /**
         * Provides the instance of {@link KeyManagerFactory}
         * @return instance of {@link KeyManagerFactory}
         */
        Optional<KeyManagerFactory> keyManagerFactory();

        /**
         * Provides the SSL provider (JDK, OpenSsl, ...) if supported by transport
         * @return SSL provider
         */
        Optional<String> sslProvider();

        /**
         * Provides desired client authentication level
         * @return client authentication level
         */
        Optional<String> clientAuth();

        /**
         * Provides the list of supported protocols
         * @return list of supported protocols
         */
        Collection<String> protocols();

        /**
         * Provides the list of supported cipher suites
         * @return list of supported cipher suites
         */
        Collection<String> cipherSuites();

        /**
         * Provides the instance of {@link TrustManagerFactory}
         * @return instance of {@link TrustManagerFactory}
         */
        Optional<TrustManagerFactory> trustManagerFactory();
    }

    /**
     * If supported, builds the {@link TransportExceptionHandler} instance for {@link Transport} instance
     * @param settings settings
     * @param transport {@link Transport} instance
     * @return if supported, builds the {@link TransportExceptionHandler} instance
     */
    Optional<TransportExceptionHandler> buildServerTransportExceptionHandler(Settings settings, Transport transport);

    /**
     * If supported, builds the {@link SSLEngine} instance for {@link Transport} instance
     * @param settings settings
     * @param transport {@link Transport} instance
     * @return if supported, builds the {@link SSLEngine} instance
     * @throws SSLException throws SSLException if the {@link SSLEngine} instance cannot be built
     */
    Optional<SSLEngine> buildSecureServerTransportEngine(Settings settings, Transport transport) throws SSLException;

    /**
     * If supported, builds the {@link SSLEngine} instance for client transport instance
     * @param settings settings
     * @param hostname host name
     * @param port port
     * @return if supported, builds the {@link SSLEngine} instance
     * @throws SSLException throws SSLException if the {@link SSLEngine} instance cannot be built
     */
    Optional<SSLEngine> buildSecureClientTransportEngine(Settings settings, String hostname, int port) throws SSLException;
}
