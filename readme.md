Работа с БД, JDBC, XML, XSLT, Maven

Консольное Java-приложение, производит обращение к таблице TEST в БД, состоящей из столбца целочисленного типа (FIELD).

Последовательность операций:
1. Проверка введенного N, при недопустимых значениях, происходи выход.
2. Класс инициализируется через setter.
3. Проверка таблицы, если в таблице имелись записи, они очищаются функцией truncateTable().
4. Производится вставка N записей в таблицу со значениями 1..N функцией insertNToDB(). Внесение данный, производится порциями по 10_000 элементов
5. После приложение запрашивает внесенные данные из таблицы.
Формирует XML-файл с помощью функции generateXMLFile() - 1.xml:
    ```xml
    <entries>
        <entry>
            <field>значение поля field</field>
        </entry>
        ...
    </entries>
    ```
6. Посредством XSLT (файл trans.xsl) функция transformXMLFile() преобразует содержимое 1.xml в 2.xml файл, имеющим вид:
    ```
    <entries>
        <entry field="значение поля field">
        ...
    </entries>
    ```
7. sumFieldFromXML() парсит 2.xml, выводя арифметическую сумму значений атрибутов field. 
