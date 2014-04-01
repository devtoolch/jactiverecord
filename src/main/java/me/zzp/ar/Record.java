package me.zzp.ar;

import java.util.Map;
import java.util.Set;
import me.zzp.ar.ex.IllegalFieldNameException;

public final class Record {
  private final Table table;
  private final Map<String, Object> values;

  Record(Table table, Map<String, Object> values) {
    this.table = table;
    this.values = values;
  }

  public Set<String> columnNames() {
    return values.keySet();
  }

  public <E> E get(String name) {
    name = name.toLowerCase();
    if (values.containsKey(name)) {
      return (E) values.get(name);
    } else if (table.relations.containsKey(name)) {
      Association relation = table.relations.get(name);
      Table active = table.dbo.active(relation.target);
      active.join(relation.assoc(table.name, getInt("id")));
      if (relation.isAncestor() && !relation.isCross()) {
        active.constrain(relation.key, getInt("id"));
      }
      return (E)(relation.isOnlyOneResult()? active.first(): active);
    } else {
      throw new IllegalFieldNameException(name);
    }
  }

  /* For primitive types */
  public boolean getBool(String name) {
    return get(name);
  }

  public byte getByte(String name) {
    return get(name);
  }

  public char getChar(String name) {
    return get(name);
  }

  public short getShort(String name) {
    return get(name);
  }

  public int getInt(String name) {
    return get(name);
  }

  public long getLong(String name) {
    return get(name);
  }

  public float getFloat(String name) {
    return get(name);
  }

  public double getDouble(String name) {
    return get(name);
  }

  /* For any other types */

  public String getStr(String name) {
    return get(name);
  }

  public <E> E get(String name, Class<E> type) {
    return type.cast(get(name));
  }

  public void set(String name, Object value) {
    name = name.toLowerCase();
    values.put(name, value);
  }

  public void save() {
    table.update(this);
  }

  public void destroy() {
    table.delete(this);
  }

  @Override
  public String toString() {
    StringBuilder line = new StringBuilder();
    for (Map.Entry<String, Object> e : values.entrySet()) {
      line.append(String.format("%s = %s\n", e.getKey(), e.getValue()));
    }
    return line.toString();
  }
}
