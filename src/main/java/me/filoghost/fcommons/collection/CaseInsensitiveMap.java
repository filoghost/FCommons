/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.collection;

import me.filoghost.fcommons.Preconditions;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CaseInsensitiveMap<V> implements Map<String, V> {

	private final Map<String, V> delegate;

	public CaseInsensitiveMap() {
		this.delegate = new HashMap<>();
	}

	@Override
	public V put(String key, V value) {
		return delegate.put(getLowercaseKey(key), value);
	}

	@Override
	public V get(Object key) {
		return delegate.get(getLowercaseKey(key));
	}

	@Override
	public boolean containsKey(Object key) {
		return delegate.containsKey(getLowercaseKey(key));
	}

	@Override
	public V remove(Object key) {
		return delegate.remove(getLowercaseKey(key));
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public boolean containsValue(Object value) {
		return delegate.containsValue(value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends V> map) {
		map.forEach(this::put);
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public Set<String> keySet() {
		return Collections.unmodifiableSet(delegate.keySet());
	}

	@Override
	public Collection<V> values() {
		return Collections.unmodifiableCollection(delegate.values());
	}

	@Override
	public Set<Entry<String, V>> entrySet() {
		return Collections.unmodifiableSet(delegate.entrySet());
	}

	private String getLowercaseKey(Object key) {
		Preconditions.notNull(key, "key");
		Preconditions.checkArgument(key instanceof String, "key must be a string");
		return ((String) key).toLowerCase();
	}

}
