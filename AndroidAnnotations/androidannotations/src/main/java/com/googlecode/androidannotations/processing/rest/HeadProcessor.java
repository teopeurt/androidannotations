/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.googlecode.androidannotations.processing.rest;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import com.googlecode.androidannotations.annotations.rest.Head;
import com.googlecode.androidannotations.processing.EBeansHolder;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JVar;

public class HeadProcessor extends MethodProcessor {

	public HeadProcessor(ProcessingEnvironment processingEnv, RestImplementationsHolder restImplementationHolder) {
		super(processingEnv, restImplementationHolder);
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return Head.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder activitiesHolder) throws Exception {

		RestImplementationHolder holder = restImplementationsHolder.getEnclosingHolder(element);
		ExecutableElement executableElement = (ExecutableElement) element;

		TypeMirror returnType = executableElement.getReturnType();

		JClass expectedClass = holder.refClass(returnType.toString());

		Head headAnnotation = element.getAnnotation(Head.class);
		String urlSuffix = headAnnotation.value();
		String url = holder.urlPrefix + urlSuffix;

		generateRestTemplateCallBlock(new MethodProcessorHolder(executableElement, url, expectedClass, expectedClass, codeModel));
	}

	@Override
	protected JInvocation addResultCallMethod(JInvocation restCall, MethodProcessorHolder methodHolder) {
		return JExpr.invoke(restCall, "getHeaders");
	}

	@Override
	protected JInvocation addHttpEntityVar(JInvocation restCall, MethodProcessorHolder methodHolder) {
		return restCall.arg(JExpr._null());
	}

	@Override
	protected JInvocation addResponseEntityArg(JInvocation restCall, MethodProcessorHolder methodHolder) {
		return restCall.arg(JExpr._null());
	}

	@Override
	protected JVar addHttpHeadersVar(JBlock body, ExecutableElement executableElement) {
		return generateHttpHeadersVar(body, executableElement);
	}

}
