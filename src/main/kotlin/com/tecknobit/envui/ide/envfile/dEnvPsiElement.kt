package com.tecknobit.envui.ide.envfile

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

class dEnvPsiElement(
    node: ASTNode,
) : ASTWrapperPsiElement(
    node
) {

    fun key(): ASTNode? {
        return node.findChildByType(EnvGeneratedTypes.KEY)
    }

    fun value(): ASTNode? {
        return node.findChildByType(EnvGeneratedTypes.VALUE)
    }

}