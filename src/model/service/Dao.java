package model.service;

import java.util.Collection;
import java.util.List;

interface Dao<Id, Entity> {
  Entity get(Id id);

  Id getId(Entity entity);

  List<Entity> getAll();

  List<Entity> getAll(Collection<Id> ids);

  void add(Id id, Entity entity);

  void deleteAll();
}
