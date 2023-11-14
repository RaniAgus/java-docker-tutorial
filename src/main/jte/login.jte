@param io.github.raniagus.example.views.LoginView view

@template.base(title = "Login", body = @`
    <main class="container">
        <h1>Iniciar sesión</h1>

        <form action="/login" method="post">
            <label for="email">Email</label>
            <input type="email" name="email" id="email" placeholder="Email" value="${view.email()}"
                   aria-invalid="${view.errors().contains("email")}"
                   autofocus="${view.errors().contains("email") || view.email().isEmpty()}"
                   required>

            <label for="password">Contraseña</label>
            <input type="password" name="password" id="password" placeholder="Contraseña"
                   aria-invalid="${view.errors().contains("password")}"
                   aria-required="true"
                   autofocus="${!view.errors().contains("email") && !view.email().isEmpty()}"
                   required>

            <input type="hidden" name="origin" value="${view.origin()}">

            <button type="submit">Login</button>
        </form>

        @if(!view.errors().isEmpty())
            <p>Usuario o contraseña incorrectos.</p>
        @endif
    </main>
`)
