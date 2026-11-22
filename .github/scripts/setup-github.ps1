<#
  Creates or updates the issue labels for Twister with colours and descriptions (ADR 0008).
  Optional: labels are also created automatically on first use, but grey and without descriptions.
  Safe to run again: existing labels are updated in place.

  Requirements: GitHub CLI (gh) logged in with access to the repository (`gh auth login`).
  Usage (from the repository root):  powershell -ExecutionPolicy Bypass -File .github\scripts\setup-github.ps1
#>
param(
    [string]$Repo = "YahorSviarchkou/twister"
)

$ErrorActionPreference = "Stop"

gh auth status *> $null
if ($LASTEXITCODE -ne 0) { throw "gh is not logged in. Run 'gh auth login' first." }

# ---------------------------------------------------------------- labels
$labels = @(
    @{ n = "module:tw-core";              c = "c5def5"; d = "Domain model" },
    @{ n = "module:tw-workflow";          c = "c5def5"; d = "Services, state machines, DAO ports" },
    @{ n = "module:tw-persistence-api";   c = "c5def5"; d = "JPA entities, repositories, mappers" },
    @{ n = "module:tw-fx-javafx-starter"; c = "c5def5"; d = "JavaFX Spring Boot starter" },
    @{ n = "module:tw-app-desktop";       c = "c5def5"; d = "Desktop assembly" },
    @{ n = "module:tw-docs";              c = "c5def5"; d = "Documentation and viewer" },
    @{ n = "module:build";                c = "c5def5"; d = "Root Gradle build, repository setup" },
    @{ n = "module:ci";                   c = "c5def5"; d = "GitHub Actions, packaging" },
    @{ n = "module:claude";               c = "c5def5"; d = "CLAUDE.md and .claude" },

    @{ n = "phase:0"; c = "fbca04"; d = "Decisions and cleanup" },
    @{ n = "phase:1"; c = "fbca04"; d = "MVP: accept, repair, hand over" },
    @{ n = "phase:2"; c = "fbca04"; d = "Mechanic and customer" },
    @{ n = "phase:3"; c = "fbca04"; d = "Stock and money" },
    @{ n = "phase:4"; c = "fbca04"; d = "Retention" },
    @{ n = "phase:5"; c = "fbca04"; d = "Product" },

    @{ n = "needs-discussion"; c = "e99695"; d = "Needs a decision from the owner" },
    @{ n = "blocked";          c = "b60205"; d = "Waiting on another issue" }
)

foreach ($l in $labels) {
    gh label create $l.n --repo $Repo --color $l.c --description $l.d --force | Out-Null
    Write-Host "label  $($l.n)"
}

Write-Host "Done."
