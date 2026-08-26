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

%state EXPECT_VALUE

EOL=\r\n|\r|\n
HORIZONTAL_SPACE=[\ \t\f]+
KEY_NAME=[A-Za-z_][A-Za-z0-9_.-]*

// Quoted values may span multiple lines. A backslash protects the following
// character, including the quote character itself.
DOUBLE_QUOTED=\"([^\"\\]|\\[^])*\"
SINGLE_QUOTED=\'([^\'\\]|\\[^])*\'
BACKTICK_QUOTED=\`([^\`\\]|\\[^])*\`

// In an unquoted value an unescaped # starts a comment. Everything else is
// data: URLs, JSON, shell expansion, equals signs, unicode, and spaces.
UNQUOTED_FIRST=([^#\r\n\ \t\f\"\'\`]|\\#)
UNQUOTED_REST=([^#\r\n]|\\#)*
UNQUOTED_VALUE={UNQUOTED_FIRST}{UNQUOTED_REST}

%%

<YYINITIAL> {
  \uFEFF              { return WHITE_SPACE; }
  {HORIZONTAL_SPACE}  { return WHITE_SPACE; }
  {EOL}               { return NEW_LINE; }
  "#"[^\r\n]*        { return COMMENT; }

  // `export` is optional in shell-compatible dotenv files. The look-ahead
  // keeps EXPORT=... usable as a normal property.
  "export"/{HORIZONTAL_SPACE} { return EXPORT; }
  {KEY_NAME}           { return KEY; }
  "="                  { yybegin(EXPECT_VALUE); return EQUALS; }
}

<EXPECT_VALUE> {
  {HORIZONTAL_SPACE}  { return WHITE_SPACE; }
  {DOUBLE_QUOTED}     { return QUOTED_VALUE; }
  {SINGLE_QUOTED}     { return QUOTED_VALUE; }
  {BACKTICK_QUOTED}   { return QUOTED_VALUE; }
  {UNQUOTED_VALUE}    { return VALUE; }
  "#"[^\r\n]*        { return COMMENT; }
  {EOL}               { yybegin(YYINITIAL); return NEW_LINE; }
}

[^] { return BAD_CHARACTER; }
