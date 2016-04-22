package com.city.support.sys.user.service;

import com.city.common.event.EsiEvent;
import com.city.common.event.listener.EsiListenerAdapter;
import com.city.common.event.watcher.DepWatched;
import com.city.common.util.ConvertUtil;
import com.city.support.sys.user.dao.PersonDao;
import com.city.support.sys.user.entity.Department;
import com.city.support.sys.user.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wys on 2016/1/13.
 */
@Service
public class DepartmentPersonService {
    @Autowired
    private PersonDao personDao;
    private DepWatched depWatched;
    @Autowired
    public DepartmentPersonService(DepWatched depWatched) {
        this.depWatched = depWatched;
        depWatched.addListener(new EsiListenerAdapter() {
            @Override
            public boolean handlerEvent(EsiEvent eEvent) {
                if(DepWatched.BEFOREDELETE.equals(eEvent.getEventName())){
                    String depId = (String)eEvent.getArgs().get(DepWatched.PARAM_DEPIDS);
                    delPersonByDep(Integer.parseInt(depId));
                }
                return true;

            }
        },null);
    }

    public List<Person> queryPersonByDep(Integer depId) {
        return personDao.queryPersonByDep(depId);
    }

    public void addPerson(List<Person> persons,Department department) {
        for (Person person : persons) {
            person.setDepartment(department);
            personDao.insert(person, true);
        }
        personDao.flush();
    }

    public void updatePerson(List<Person> persons) {
        ConvertUtil<Person> cver = new ConvertUtil<>();
        Person p = null;
        for(Person person:persons){
            p = personDao.queryById(person.getId());
            cver.replication(person,p,Person.class.getName());
            personDao.update(p,true);
        }
        personDao.flush();
    }

    public void delPerson(List<Person> persons) {
        for (Person person:persons){
            personDao.delete(person,true);
        }
        personDao.flush();
    }
    public void delPersonByDep(Integer depId) {
        personDao.delPersonByDep(depId);
    }
}
