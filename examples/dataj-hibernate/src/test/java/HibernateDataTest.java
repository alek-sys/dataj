/*
 * Copyright 2017 Alexey Nesterov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.Before;
import org.junit.Test;

public class HibernateDataTest {

    private SessionFactory sessionFactory;

    @Before
    public void setUp() throws Exception {

        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                .applySetting("hibernate.connection.url", "jdbc:h2:mem:test")
                .applySetting("hibernate.hbm2ddl.auto", "create-drop")
                .build();

        sessionFactory = new MetadataSources(serviceRegistry)
                .addAnnotatedClass(CountryData.class)
                .addAnnotatedClass(CityData.class)
                .buildMetadata()
                .buildSessionFactory();
    }

    @Test
    public void shouldPersistDataObjects() {
        CountryData uk = new CountryData(0L, "UK", "GBR", new CityData(0L, "London"));
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(uk);

            transaction.commit();
        }
    }
}
