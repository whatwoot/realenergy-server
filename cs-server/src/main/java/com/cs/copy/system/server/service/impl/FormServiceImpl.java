package com.cs.copy.system.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs.copy.system.api.entity.Form;
import com.cs.copy.system.api.service.FormService;
import com.cs.copy.system.server.mapper.FormMapper;
import org.springframework.stereotype.Service;

@Service
public class FormServiceImpl extends ServiceImpl<FormMapper, Form> implements FormService {

}
