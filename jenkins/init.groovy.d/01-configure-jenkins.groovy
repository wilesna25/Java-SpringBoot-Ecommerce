// Jenkins Initialization Script
// Configures Jenkins on first startup

import jenkins.model.*
import hudson.security.*
import hudson.tools.*
import hudson.plugins.git.*
import org.jenkinsci.plugins.workflow.libs.GlobalLibraries
import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration
import org.jenkinsci.plugins.workflow.libs.SCMSourceRetriever
import jenkins.plugins.git.GitSCMSource

// Disable setup wizard
def instance = Jenkins.getInstance()

// Configure security
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount("admin", System.getenv("JENKINS_ADMIN_PASSWORD") ?: "admin123")
instance.setSecurityRealm(hudsonRealm)

def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)
instance.setAuthorizationStrategy(strategy)

// Save configuration
instance.save()

println "✅ Jenkins configured successfully"

