/**
 * 
 */
package org.carrot2.examples.core;

import java.util.HashMap;
import java.util.Map;

import org.carrot2.core.attribute.*;
import org.carrot2.examples.ExampleUtils;
import org.carrot2.source.yahoo.YahooDocumentSource;
import org.carrot2.source.yahoo.YahooNewsSearchService;

/**
 * An example showing how developers can use the low-level core utilities for processing
 * Carrot2 component attribute descriptors.
 */
public class WorkingWithAttributeDescriptors
{
    public static void main(String [] args) throws InstantiationException
    {
        YahooDocumentSource yahooDocumentSource = new YahooDocumentSource();

        // Descriptors for a component with default initialization attribute values
        BindableDescriptor descriptor = BindableDescriptorBuilder
            .buildDescriptor(yahooDocumentSource);
        displayDescriptor(descriptor, 0);
        System.out.println("\n");

        // Notice that for some values of initialization attribute values,
        // you may get different attribute descriptors
        Map<String, Object> initAttributes = new HashMap<String, Object>();
        initAttributes.put(YahooDocumentSource.class.getName() + ".service",
            YahooNewsSearchService.class);
        AttributeBinder
            .bind(yahooDocumentSource, initAttributes, Init.class, Input.class);

        descriptor = BindableDescriptorBuilder.buildDescriptor(yahooDocumentSource);
        displayDescriptor(descriptor, 0);
        System.out.println("\n");

        // You can also get descriptors for a subset of component's attributes
        // (in the case below, only input attributes bound at processing time)
        descriptor = BindableDescriptorBuilder.buildDescriptor(yahooDocumentSource).only(
            Input.class, Processing.class);
        displayDescriptor(descriptor, 0);
        System.out.println("\n");

        // You can also flatten the descriptor tree to a linear list
        descriptor = BindableDescriptorBuilder.buildDescriptor(yahooDocumentSource).only(
            Input.class, Processing.class).flatten();
        displayDescriptor(descriptor, 0);
    }

    private static void displayDescriptor(BindableDescriptor descriptor, int indent)
    {
        for (AttributeDescriptor attributeDescriptor : descriptor.attributeDescriptors
            .values())
        {
            System.out.print(ExampleUtils.getIndent(indent));
            System.out.println(attributeDescriptor.metadata.getLabel() + ": "
                + attributeDescriptor.metadata.getTitle());
        }

        for (Map.Entry<String, BindableDescriptor> entry : descriptor.bindableDescriptors
            .entrySet())
        {
            final BindableDescriptor subdescriptor = entry.getValue();
            System.out.print(ExampleUtils.getIndent(indent));
            System.out.println(subdescriptor.metadata.getLabel());
            displayDescriptor(subdescriptor, indent + 2);
        }
    }
}
