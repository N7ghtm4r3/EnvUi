package com.tecknobit.envui.utils

import com.intellij.openapi.application.EDT
import com.intellij.openapi.application.edtWriteAction
import com.intellij.openapi.application.readAction
import com.intellij.openapi.application.smartReadAction
import com.intellij.openapi.project.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Method used to execute a write action on the `EDT`
 *
 * @param T The type returned by the write action
 * @param writing The write action to execute
 *
 * @return the result of the write action as [T]
 *
 * @since 1.0.1
 */
suspend inline fun <T> writeOnEdt(
    noinline writing: () -> T
): T {
    return withContext(Dispatchers.EDT) {
        edtWriteAction(
            action = writing
        )
    }
}

/**
 * Method used to execute a read action in smart mode on a background thread
 *
 * @param T The type returned by the read action
 * @param project The project whose indexes must be available
 * @param reading The read action to execute
 *
 * @return the result of the read action as [T]
 *
 * @since 1.0.1
 */
suspend inline fun <T> smartReadOnBgt(
    project: Project,
    noinline reading: () -> T
): T {
    return withContext(Dispatchers.Default) {
        smartReadAction(
            project = project,
            action = reading
        )
    }
}

/**
 * Method used to execute a read action on a background thread
 *
 * @param T The type returned by the read action
 * @param reading The read action to execute
 *
 * @return the result of the read action as [T]
 *
 * @since 1.0.1
 */
suspend inline fun <T> readOnBgt(
    noinline reading: () -> T
): T {
    return withContext(Dispatchers.Default) {
        readAction(
            action = reading
        )
    }
}