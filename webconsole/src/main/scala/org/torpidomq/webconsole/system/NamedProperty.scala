package org.torpidomq.webconsole.system

class NamedProperty(final val name: String, final val value: String) {

    override def hashCode: Int = {
        if (name == null) {
            return 0;
        }
        name.hashCode
    }

}