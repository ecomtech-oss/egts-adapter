# Модуль egts-starter
Предназначен
для того, чтобы изолировать модуль library от зависимостей Spring для использования ее в не-Spring проектах
для подключения библиотеки с уже инициализированными бинами энкодеров в Spring проекты

включает в себя конфигурацию, инициализирующую бины энкодеров при наличии проперти

    egts.initialize-encoders=true

а также тест, проверяющий инициализацию контекста Spring и наличие бина энкодера в нем

для подкючения стартера достаточно добавить его зависимость

    implementation("tech.ecom.egts:starter:<VERSION>")

и проперти

    egts:
        initialize-encoders: true

далее собирать дата классы пакетов, кодировать их, либо получать дата классы пакетов из бинарных массивов как показано в sample-app