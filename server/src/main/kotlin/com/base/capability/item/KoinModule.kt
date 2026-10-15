package com.base.capability.item

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun itemKoinModule() = module { singleOf(::ItemRepositoryImpl).bind<ItemRepository>() }
