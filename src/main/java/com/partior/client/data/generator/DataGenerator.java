package com.partior.client.data.generator;

import com.partior.client.data.Role;
import com.partior.client.data.entity.SamplePerson;
import com.partior.client.data.entity.User;
import com.partior.client.data.service.SamplePersonRepository;
import com.partior.client.data.service.UserRepository;
import com.partior.client.util.JsonClientWrapper;
import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringComponent
public class DataGenerator {



    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, SamplePersonRepository samplePersonRepository,
            UserRepository userRepository, @Value("${cbdc.operator:MAS}")String cbdcOperator) {
        return args -> {

            Logger logger = LoggerFactory.getLogger(getClass());
            if (samplePersonRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }

            logger.info("Generating users data");

            if(cbdcOperator.equalsIgnoreCase("MAS")) {

                User mas = new User();
                mas.setName("Monetary Bank of Singapore");
                mas.setUsername("MAS");
                mas.setHashedPassword(passwordEncoder.encode("admin"));
                mas.setProfilePictureUrl("https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
                mas.setRoles(Set.of(Role.USER, Role.ADMIN));
                userRepository.save(mas);


                User sgBank1 = new User();
                sgBank1.setName("SG Bank 1");
                sgBank1.setUsername("SGBANK1");
                sgBank1.setHashedPassword(passwordEncoder.encode("user"));
                sgBank1.setProfilePictureUrl("https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
                sgBank1.setRoles(Set.of(Role.USER));
                userRepository.save(sgBank1);


                User sgBank2 = new User();
                sgBank2.setName("SG Bank 2");
                sgBank2.setUsername("SGBANK2");
                sgBank2.setHashedPassword(passwordEncoder.encode("user"));
                sgBank2.setProfilePictureUrl("https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
                sgBank2.setRoles(Set.of(Role.USER));
                userRepository.save(sgBank2);


                User sgBank3 = new User();
                sgBank3.setName("SG Bank 3");
                sgBank3.setUsername("SGBANK3");
                sgBank3.setHashedPassword(passwordEncoder.encode("user"));
                sgBank3.setProfilePictureUrl("https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
                sgBank3.setRoles(Set.of(Role.USER));
                userRepository.save(sgBank3);


                User sgBank4 = new User();
                sgBank4.setName("SG Bank 4");
                sgBank4.setUsername("SGBANK4");
                sgBank4.setHashedPassword(passwordEncoder.encode("user"));
                sgBank4.setProfilePictureUrl("https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
                sgBank4.setRoles(Set.of(Role.USER));
                userRepository.save(sgBank4);

                User sgBank5 = new User();
                sgBank5.setName("SG Bank 5");
                sgBank5.setUsername("SGBANK5");
                sgBank5.setHashedPassword(passwordEncoder.encode("user"));
                sgBank5.setProfilePictureUrl("https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
                sgBank5.setRoles(Set.of(Role.USER));
                userRepository.save(sgBank5);


                User sgBank6 = new User();
                sgBank6.setName("SG Bank 6");
                sgBank6.setUsername("SGBANK6");
                sgBank6.setHashedPassword(passwordEncoder.encode("user"));
                sgBank6.setProfilePictureUrl("https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
                sgBank6.setRoles(Set.of(Role.USER));
                userRepository.save(sgBank6);


                User sgBank7 = new User();
                sgBank7.setName("SG Bank 7");
                sgBank7.setUsername("SGBANK7");
                sgBank7.setHashedPassword(passwordEncoder.encode("user"));
                sgBank7.setProfilePictureUrl("https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
                sgBank7.setRoles(Set.of(Role.USER));
                userRepository.save(sgBank7);
            }


            if(cbdcOperator.equalsIgnoreCase("BI")) {
                User boi = new User();
                boi.setName("Bank of Indonesia");
                boi.setUsername("BI");
                boi.setHashedPassword(passwordEncoder.encode("admin"));
                boi.setProfilePictureUrl("https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
                boi.setRoles(Set.of(Role.USER, Role.ADMIN));
                userRepository.save(boi);

                User indoBank1 = new User();
                indoBank1.setName("Indo Bank 1");
                indoBank1.setUsername("INDOBANK1");
                indoBank1.setHashedPassword(passwordEncoder.encode("user"));
                indoBank1.setProfilePictureUrl("https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
                indoBank1.setRoles(Set.of(Role.USER));
                userRepository.save(indoBank1);


                User indoBank2 = new User();
                indoBank2.setName("Indo Bank 2");
                indoBank2.setUsername("INDOBANK2");
                indoBank2.setHashedPassword(passwordEncoder.encode("user"));
                indoBank2.setProfilePictureUrl("https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
                indoBank2.setRoles(Set.of(Role.USER));
                userRepository.save(indoBank2);


                User indoBank3 = new User();
                indoBank3.setName("Indo Bank 3");
                indoBank3.setUsername("INDOBANK3");
                indoBank3.setHashedPassword(passwordEncoder.encode("user"));
                indoBank3.setProfilePictureUrl("https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
                indoBank3.setRoles(Set.of(Role.USER));
                userRepository.save(indoBank3);


                User indoBank4 = new User();
                indoBank4.setName("Indo Bank 4");
                indoBank4.setUsername("INDOBANK4");
                indoBank4.setHashedPassword(passwordEncoder.encode("user"));
                indoBank4.setProfilePictureUrl("https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
                indoBank4.setRoles(Set.of(Role.USER));
                userRepository.save(indoBank4);

            }

            if(cbdcOperator.equalsIgnoreCase("RTGS")) {
                User rtgsSg = new User();
                rtgsSg.setName("RTGS SG");
                rtgsSg.setUsername("RTGSSG");
                rtgsSg.setHashedPassword(passwordEncoder.encode("rtgs"));
                rtgsSg.setProfilePictureUrl("https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
                rtgsSg.setRoles(Set.of(Role.RTGS));
                userRepository.save(rtgsSg);


                User rtgsBi = new User();
                rtgsBi.setName("RTGS BI");
                rtgsBi.setUsername("RTGSBI");
                rtgsBi.setHashedPassword(passwordEncoder.encode("rtgs"));
                rtgsBi.setProfilePictureUrl("https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
                rtgsBi.setRoles(Set.of(Role.RTGS));
                userRepository.save(rtgsBi);
            }

            if(cbdcOperator.equalsIgnoreCase("CSD")) {
                User csdSg = new User();
                csdSg.setName("SG CSD");
                csdSg.setUsername("SGCSD");
                csdSg.setHashedPassword(passwordEncoder.encode("csd"));
                csdSg.setProfilePictureUrl("https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
                csdSg.setRoles(Set.of(Role.CSD));
                userRepository.save(csdSg);


                User csdBi = new User();
                csdBi.setName("CSD BI");
                csdBi.setUsername("CSDBI");
                csdBi.setHashedPassword(passwordEncoder.encode("csd"));
                csdBi.setProfilePictureUrl("https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
                csdBi.setRoles(Set.of(Role.CSD));
                userRepository.save(csdBi);
            }

            logger.info("Generated demo for:{}", cbdcOperator);



        };
    }

}