/*
 * Created on 29-Aug-2004
 * 
 * (c) 2003-2004 ThoughtWorks Ltd
 *
 * See license.txt for license details
 */
package com.thoughtworks.jbehave.extensions.story.domain;

import com.thoughtworks.jbehave.core.verify.Verify;
import com.thoughtworks.jbehave.extensions.jmock.UsingJMock;
import com.thoughtworks.jbehave.extensions.story.base.Given;
import com.thoughtworks.jbehave.extensions.story.visitor.Visitable;
import com.thoughtworks.jbehave.extensions.story.visitor.Visitor;

/**
 * @author <a href="mailto:dan.north@thoughtworks.com">Dan North</a>
 */
public class SimpleContextBehaviour extends UsingJMock {

    public void shouldPassItselfIntoVisitor() throws Exception {
        // given...
        Visitable context = new Context(new Given[0]);
        Mock visitor = new Mock(Visitor.class);
        visitor.expects(once()).method("visitContext").with(same(context));

        // when...
        context.accept((Visitor)visitor.proxy());
        
        // then... verified by pixies
    }
    
    public void shouldTellGivensToAcceptVisitorInOrder() throws Exception {
        // given...
        Mock given1 = new Mock(Given.class, "given1");
        Mock given2 = new Mock(Given.class, "given2");
        Visitor visitor = (Visitor) stub(Visitor.class);
        
        Context context = new Context(
                new Given[] {
                        (Given) given1.proxy(),
                        (Given) given2.proxy()
                }
        );
        
        given1.expects(once()).method("accept").with(same(visitor));
        given2.expects(once()).method("accept").with(same(visitor)).after(given1, "accept");
        
        // when...
        context.accept(visitor);
        
        // then... verified by framework
    }
    
    private static class SomeCheckedException extends Exception {
    }
    
    public void shouldPropagateExceptionFromCallToGivensAcceptMethod() throws Exception {
        // given...
        Visitor visitorStub = (Visitor)stub(Visitor.class);
        Mock given = new Mock(Given.class);
        Context context = new Context((Given)given.proxy());

        // expect...
        given.expects(atLeastOnce()).method("accept").will(throwException(new SomeCheckedException()));
        
        // when...
        try {
            context.accept(visitorStub);
            Verify.impossible("Should have propagated SomeCheckedException");
        } catch (SomeCheckedException expected) {
        }
    }
}
