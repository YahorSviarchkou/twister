package com.twister.desktop;

import com.twister.workflow.WorkflowConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** The assembly: the screens below this package, and the modules through their auto-configurations. */
// TODO(tw-10): remove the exclusion once the DAO ports have adapters. The workflow services need them, so with the
// auto-configuration the context fails on the first missing one.
@SpringBootApplication(exclude = WorkflowConfiguration.class)
public class TwisterDesktopApplication {}
