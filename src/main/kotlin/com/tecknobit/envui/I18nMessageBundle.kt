package com.tecknobit.envui

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.PropertyKey
import java.util.function.Supplier

/**
 * `BUNDLE` the path of the internationalization resource bundle
 */
private const val BUNDLE = "messages.I18nMessageBundle"

/**
 * The `I18nMessageBundle` object allows to retrieve localized plugin messages
 *
 * @author N7ghtm4r3 - Tecknobit
 */
internal object I18nMessageBundle {

    /**
     * `instance` the dynamic resource bundle used to resolve localized messages
     */
    private val instance = DynamicBundle(I18nMessageBundle::class.java, BUNDLE)

    /**
     * Method used to resolve a localized message and interpolate its parameters
     *
     * @param key The resource key of the message
     * @param params The parameters to interpolate in the message
     *
     * @return the localized message as [String]
     */
    @JvmStatic
    fun message(
        @PropertyKey(resourceBundle = BUNDLE)
        key: String,
        vararg params: Any?,
    ): @Nls String {
        return instance.getMessage(
            key = key,
            *params
        )
    }

    /**
     * Method used to create a supplier that lazily resolves a localized message
     *
     * @param key The resource key of the message
     * @param params The parameters to interpolate in the message
     *
     * @return the localized message supplier as [Supplier]
     */
    @JvmStatic
    fun lazyMessage(
        @PropertyKey(resourceBundle = BUNDLE)
        key: String,
        vararg params: Any?,
    ): Supplier<@Nls String> {
        return instance.getLazyMessage(
            key = key,
            *params
        )
    }

}
