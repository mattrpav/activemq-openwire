/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.openwire.generator;

import org.codehaus.jam.JAnnotation;
import org.codehaus.jam.JAnnotationValue;
import org.codehaus.jam.JClass;
import org.codehaus.jam.JField;
import org.codehaus.jam.JMethod;
import org.codehaus.jam.JProperty;
import org.codehaus.jam.JamClassIterator;
import org.codehaus.jam.JamService;

/**
 *
 */
public abstract class OpenWireGenerator {

    protected int openwireVersion;
    protected String filePostFix = ".java";
    protected JamService jam;

    protected String commandsPackage = "org.apache.activemq.openwire.commands";
    protected String codecPackageRoot = "org.apache.activemq.openwire.codec";

    public boolean isValidProperty(JProperty it) {
        JMethod getter = it.getGetter();
        JMethod setter = it.getSetter();

        if (getter == null || setter == null || getter.isStatic()) {
            return false;
        }

        JAnnotation annotation = getter.getAnnotation("openwire:property");
        if (annotation == null) {
            return false;
        }

        return true;
    }

    public boolean includeInThisVersion(JAnnotation annotation) {
        JAnnotationValue value = annotation.getValue("version");
        if (value != null && value.asInt() <= getOpenwireVersion()) {
            return true;
        }

        return false;
    }

    public boolean isCachedProperty(JProperty it) {
        JMethod getter = it.getGetter();
        if (!isValidProperty(it)) {
            return false;
        }
        JAnnotationValue value = getter.getAnnotation("openwire:property").getValue("cache");
        return value != null && value.asBoolean();
    }

    public boolean isAbstract(JClass j) {
        JField[] fields = j.getFields();
        for (int i = 0; i < fields.length; i++) {
            JField field = fields[i];
            if (field.isStatic() && field.isPublic() && field.isFinal() && field.getSimpleName().equals("DATA_STRUCTURE_TYPE")) {
                return false;
            }
        }
        return true;
    }

    public boolean isThrowable(JClass j) {
        if (j.getQualifiedName().equals(Throwable.class.getName())) {
            return true;
        }
        return j.getSuperclass() != null && isThrowable(j.getSuperclass());
    }

    public boolean isMarshallAware(JClass j) {
        if (filePostFix.endsWith("java")) {
            JClass[] interfaces = j.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                if (interfaces[i].getQualifiedName().equals("org.apache.activemq.command.MarshallAware")) {
                    return true;
                }
            }
            return false;
        } else {
            String simpleName = j.getSimpleName();
            return simpleName.equals("ActiveMQMessage") || simpleName.equals("WireFormatInfo");
        }
    }

    public JamService getJam() {
        return jam;
    }

    public JamClassIterator getClasses() {
        return getJam().getClasses();
    }

    public int getOpenwireVersion() {
        return openwireVersion;
    }

    public void setOpenwireVersion(int openwireVersion) {
        this.openwireVersion = openwireVersion;
    }

    public String getCommandsPackage() {
        return commandsPackage;
    }

    public void setCommandsPackage(String commandsPacakge) {
        this.commandsPackage = commandsPacakge;
    }

    public String getCodecPackageRoot() {
        return codecPackageRoot;
    }

    public void setCodecPackageRoot(String codecPackageRoot) {
        this.codecPackageRoot = codecPackageRoot;
    }

    public String getOpenWireOpCode(JClass element) {
        if (element != null) {
            JAnnotation annotation = element.getAnnotation("openwire:marshaller");
            return stringValue(annotation, "code", "0");
        }
        return "0";
    }

    protected String stringValue(JAnnotation annotation, String name) {
        return stringValue(annotation, name, null);
    }

    protected String stringValue(JAnnotation annotation, String name, String defaultValue) {
        if (annotation != null) {
            JAnnotationValue value = annotation.getValue(name);
            if (value != null) {
                return value.asString();
            }
        }
        return defaultValue;
    }

    public void setJam(JamService jam) {
        this.jam = jam;
    }

    public String decapitalize(String text) {
        if (text == null) {
            return null;
        }
        return text.substring(0, 1).toLowerCase() + text.substring(1);
    }

    public String capitalize(String text) {
        if (text == null) {
            return null;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
