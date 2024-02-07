@param io.github.raniagus.example.views.ErrorView view

@template.base(
title = "Error",
body = @`
    <main class="container">
        <section>
            <h1>${view.title()}</h1>
            <p>${view.message()}</p>
            <a href="/">Volver al inicio</a>
        </section>
    </main>
`)
