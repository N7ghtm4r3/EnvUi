package com.tecknobit.envui.ide.envfile;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static com.tecknobit.envui.ide.envfile.EnvGeneratedTypes.*;

%%

%{
  public _dEnvLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _dEnvLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+


%%
<YYINITIAL> {
  {WHITE_SPACE}       { return WHITE_SPACE; }

  "KEY"               { return KEY; }
  "VALUE"             { return VALUE; }
  "QUOTED_VALUE"      { return QUOTED_VALUE; }
  "COMMENT"           { return COMMENT; }
  "NEW_LINE"          { return NEW_LINE; }


}

[^] { return BAD_CHARACTER; }
