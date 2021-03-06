// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Implements common functionality for the many record types whose format is a
 * list of strings.
 * 
 * @author Brian Wellington
 */

abstract class TXTBase extends Record {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4319510507246305931L;

	/** The strings. */
	protected List strings;

	/**
	 * Instantiates a new tXT base.
	 */
	protected TXTBase() {
	}

	/**
	 * Instantiates a new tXT base.
	 * 
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param dclass
	 *            the dclass
	 * @param ttl
	 *            the ttl
	 */
	protected TXTBase(Name name, int type, int dclass, long ttl) {
		super(name, type, dclass, ttl);
	}

	/**
	 * Instantiates a new tXT base.
	 * 
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param dclass
	 *            the dclass
	 * @param ttl
	 *            the ttl
	 * @param strings
	 *            the strings
	 */
	protected TXTBase(Name name, int type, int dclass, long ttl, List strings) {
		super(name, type, dclass, ttl);
		if (strings == null)
			throw new IllegalArgumentException("strings must not be null");
		this.strings = new ArrayList(strings.size());
		Iterator it = strings.iterator();
		try {
			while (it.hasNext()) {
				String s = (String) it.next();
				this.strings.add(byteArrayFromString(s));
			}
		} catch (TextParseException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	/**
	 * Instantiates a new tXT base.
	 * 
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param dclass
	 *            the dclass
	 * @param ttl
	 *            the ttl
	 * @param string
	 *            the string
	 */
	protected TXTBase(Name name, int type, int dclass, long ttl, String string) {
		this(name, type, dclass, ttl, Collections.singletonList(string));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xbill.DNS.Record#rrFromWire(org.xbill.DNS.DNSInput)
	 */
	void rrFromWire(DNSInput in) throws IOException {
		strings = new ArrayList(2);
		while (in.remaining() > 0) {
			byte[] b = in.readCountedString();
			strings.add(b);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xbill.DNS.Record#rdataFromString(org.xbill.DNS.Tokenizer,
	 * org.xbill.DNS.Name)
	 */
	void rdataFromString(Tokenizer st, Name origin) throws IOException {
		strings = new ArrayList(2);
		while (true) {
			Tokenizer.Token t = st.get();
			if (!t.isString())
				break;
			try {
				strings.add(byteArrayFromString(t.value));
			} catch (TextParseException e) {
				throw st.exception(e.getMessage());
			}

		}
		st.unget();
	}

	/**
	 * converts to a String.
	 * 
	 * @return the string
	 */
	String rrToString() {
		StringBuffer sb = new StringBuffer();
		Iterator it = strings.iterator();
		while (it.hasNext()) {
			byte[] array = (byte[]) it.next();
			sb.append(byteArrayToString(array, true));
			if (it.hasNext())
				sb.append(" ");
		}
		return sb.toString();
	}

	/**
	 * Returns the text strings.
	 * 
	 * @return A list of Strings corresponding to the text strings.
	 */
	public List getStrings() {
		List list = new ArrayList(strings.size());
		for (int i = 0; i < strings.size(); i++)
			list.add(byteArrayToString((byte[]) strings.get(i), false));
		return list;
	}

	/**
	 * Returns the text strings.
	 * 
	 * @return A list of byte arrays corresponding to the text strings.
	 */
	public List getStringsAsByteArrays() {
		return strings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xbill.DNS.Record#rrToWire(org.xbill.DNS.DNSOutput,
	 * org.xbill.DNS.Compression, boolean)
	 */
	void rrToWire(DNSOutput out, Compression c, boolean canonical) {
		Iterator it = strings.iterator();
		while (it.hasNext()) {
			byte[] b = (byte[]) it.next();
			out.writeCountedString(b);
		}
	}

}
