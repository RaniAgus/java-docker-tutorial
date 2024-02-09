@import io.github.raniagus.example.constants.Params
@param io.github.raniagus.example.views.LoginView view

!{var hasEmailError = view.errors().contains(Params.EMAIL);}

@template.base(
    title = "Login",
    body = @`
        <main class="container">
            <h1>Iniciar sesión</h1>

            <form action="/login" method="post">
                <label for="email">Email</label>
                <input type="email" name="${Params.EMAIL}" id="email"
                       placeholder="Email" value="${view.email()}"
                       aria-invalid="${view.email().isBlank() ? null : Boolean.toString(hasEmailError)}"
                       autofocus="${view.email().isBlank() || hasEmailError}"
                       required>

                <label for="password">Contraseña</label>
                <input type="password" name="${Params.PASSWORD}" id="password"
                       placeholder="Contraseña"
                       aria-invalid="${view.errors().contains(Params.PASSWORD)}"
                       autofocus="${view.errors().contains(Params.PASSWORD)}"
                       required>

                <input type="hidden" name="${Params.ORIGIN}" value="${view.origin()}">

                <button type="submit">Login</button>
            </form>

            @if (!view.errors().isEmpty())
                <p>Usuario o contraseña incorrectos.</p>
            @endif
        </main>
`)
