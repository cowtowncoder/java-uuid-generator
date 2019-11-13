// NOTE: auto-generated with Moditect plugin using "-Pmoditect", on 22-Mar-2019
module com.fasterxml.uuid {
    requires org.slf4j;

    exports com.fasterxml.uuid;
    // despite name, contains classes users may want to use directly so:
    exports com.fasterxml.uuid.impl;
    // but no user-serviceable parts here, I think
//    exports com.fasterxml.uuid.ext;
}
