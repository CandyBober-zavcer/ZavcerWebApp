package ru.yarsu.web

import ru.yarsu.web.templates.ContextAwarePebbleTemplates
import ru.yarsu.web.templates.ContextAwareTemplateRenderer

fun rendererProvider(dir: Boolean): ContextAwareTemplateRenderer =
    if (dir) {
        ContextAwarePebbleTemplates().HotReload("src/main/resources/")
    } else {
        ContextAwarePebbleTemplates().CachingClasspath()
    }
