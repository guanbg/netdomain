package com.platform.cubism.expression;

class Token {
	TokenKind kind;
	String data;
	int startpos; // index of first character
	int endpos; // index of char after the last character

	Token(TokenKind tokenKind, int startpos, int endpos) {
		this.kind = tokenKind;
		this.startpos = startpos;
		this.endpos = endpos;
	}

	Token(TokenKind tokenKind, char[] tokenData, int pos, int endpos) {
		this(tokenKind, pos, endpos);
		this.data = new String(tokenData);
	}

	public TokenKind getKind() {
		return kind;
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("[").append(kind.toString());
		if (kind.hasPayload()) {
			s.append(":").append(data);
		}
		s.append("]");
		s.append("(").append(startpos).append(",").append(endpos).append(")");
		return s.toString();
	}

	public boolean isIdentifier() {
		return kind == TokenKind.IDENTIFIER;
	}

	public boolean isNumericRelationalOperator() {
		return kind == TokenKind.GT || kind == TokenKind.GE || kind == TokenKind.LT || kind == TokenKind.LE || kind == TokenKind.EQ || kind == TokenKind.NE;
	}

	public String stringValue() {
		return data;
	}

	public Token asInstanceOfToken() {
		return new Token(TokenKind.INSTANCEOF, startpos, endpos);
	}

	public Token asMatchesToken() {
		return new Token(TokenKind.MATCHES, startpos, endpos);
	}

	public Token asBetweenToken() {
		return new Token(TokenKind.BETWEEN, startpos, endpos);
	}
}