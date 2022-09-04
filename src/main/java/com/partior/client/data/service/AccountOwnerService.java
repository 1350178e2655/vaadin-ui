package com.partior.client.data.service;

import com.partior.client.data.entity.SamplePerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AccountOwnerService {


    private final SamplePersonRepository repository;

    @Autowired
    public AccountOwnerService(SamplePersonRepository repository) {
        this.repository = repository;
    }


       public Page<SamplePerson> list(Pageable pageable) {

        return repository.findAll(pageable);
    }


}
