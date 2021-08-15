package com.booking.challenge;

import com.booking.challenge.auth.ExampleAuthenticator;
import com.booking.challenge.auth.ExampleAuthorizer;
import com.booking.challenge.cli.RenderCommand;
import com.booking.challenge.core.FlightSchedule;
import com.booking.challenge.core.Template;
import com.booking.challenge.core.User;
import com.booking.challenge.db.FlightScheduleDAO;
import com.booking.challenge.filter.DateRequiredFeature;
import com.booking.challenge.health.TemplateHealthCheck;
import com.booking.challenge.resources.FlightScheduleResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import java.util.Map;

public class FlightBookingApplication extends Application<FlightBookingConfiguration> {
    public static void main(String[] args) throws Exception {
        new FlightBookingApplication().run(args);
    }

    private final HibernateBundle<FlightBookingConfiguration> hibernateBundle =
        new HibernateBundle<FlightBookingConfiguration>(FlightSchedule.class) {
            @Override
            public DataSourceFactory getDataSourceFactory(FlightBookingConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        };

    @Override
    public String getName() {
        return "booking-challenge";
    }

    @Override
    public void initialize(Bootstrap<FlightBookingConfiguration> bootstrap) {
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        bootstrap.addCommand(new RenderCommand());
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new MigrationsBundle<FlightBookingConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(FlightBookingConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new ViewBundle<FlightBookingConfiguration>() {
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(FlightBookingConfiguration configuration) {
                return configuration.getViewRendererConfiguration();
            }
        });
    }

    @Override
    public void run(FlightBookingConfiguration configuration, Environment environment) {
        final FlightScheduleDAO flightScheduleDAO = new FlightScheduleDAO(hibernateBundle.getSessionFactory());
        final Template template = configuration.buildTemplate();

        environment.healthChecks().register("template", new TemplateHealthCheck(template));
        environment.jersey().register(DateRequiredFeature.class);
        environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                .setAuthenticator(new ExampleAuthenticator())
                .setAuthorizer(new ExampleAuthorizer())
                .setRealm("SUPER SECRET STUFF")
                .buildAuthFilter()));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new FlightScheduleResource(flightScheduleDAO));
    }
}
