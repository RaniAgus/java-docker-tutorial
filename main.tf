variable "cockroach_api_key" {
  type      = string
  nullable  = false
  sensitive = true
}

variable "cockroach_provider" {
  type     = string
  nullable = false
  default  = "gcp"
}

variable "cockroach_region" {
  type     = string
  nullable = false
  default  = "southamerica-east1"
}

variable "do_token" {
  type      = string
  nullable  = false
  sensitive = true
}

variable "sql_database_name" {
  type     = string
  nullable = false
  default  = "example"
}

variable "sql_user_name" {
  type     = string
  nullable = false
  default  = "maxroach"
}

variable "sql_user_password" {
  type      = string
  nullable  = false
  sensitive = true
}

terraform {
  required_providers {
    cockroach = {
      source = "cockroachdb/cockroach"
    }

    digitalocean = {
      source  = "digitalocean/digitalocean"
      version = "~> 2.0"
    }
  }
}

provider "cockroach" {
  apikey = var.cockroach_api_key
}

provider "digitalocean" {
  token = var.do_token
}

resource "cockroach_cluster" "sql_cluster" {
  name = "java-docker-tutorial"
  cloud_provider = upper(var.cockroach_provider)
  serverless = {
    spend_limit = 0
  }
  regions = [{
    name = var.cockroach_region
  }]
}

resource "cockroach_sql_user" "sql_user" {
  name       = var.sql_user_name
  password   = var.sql_user_password
  cluster_id = cockroach_cluster.sql_cluster.id
}

resource "cockroach_database" "sql_database" {
  name       = var.sql_database_name
  cluster_id = cockroach_cluster.sql_cluster.id
}

resource "digitalocean_app" "java-docker-tutorial" {
  spec {
    name   = "java-docker-tutorial"
    region = "ams"

    env {
      key   = "DATABASE_URL"
      value = "jdbc:postgresql://${cockroach_cluster.sql_cluster.regions[0].sql_dns}:26257/${var.sql_database_name}"
      scope = "RUN_TIME"
      type  = "SECRET"
    }

    env {
      key   = "DATABASE_USERNAME"
      value = var.sql_user_name
      scope = "RUN_TIME"
      type  = "SECRET"
    }

    env {
      key   = "DATABASE_PASSWORD"
      value = var.sql_user_password
      scope = "RUN_TIME"
      type  = "SECRET"
    }

    service {
      name               = "web"
      instance_count     = 1
      instance_size_slug = "basic-xxs"
      http_port          = 8080

      image {
        registry_type = "DOCR"
        registry      = "raniagus"
        repository    = "java_docker_tutorial"
        tag           = "web"

        deploy_on_push {
          enabled = true
        }
      }

      env {
        key   = "DATABASE_HBM2DDL_AUTO"
        value = "none"
        scope = "RUN_TIME"
      }
    }

    worker {
      name               = "cron"
      instance_count     = 1
      instance_size_slug = "basic-xxs"

      image {
        registry_type = "DOCR"
        registry      = "raniagus"
        repository    = "java_docker_tutorial"
        tag           = "cron"

        deploy_on_push {
          enabled = true
        }
      }

      env {
        key   = "DATABASE_HBM2DDL_AUTO"
        value = "create-drop"
        scope = "RUN_TIME"
      }
    }
  }
}
