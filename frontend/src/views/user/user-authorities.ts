import {customElement, html, LitElement} from 'lit-element';
import '@vaadin/vaadin-text-field';
import '@vaadin/vaadin-button';
import '@vaadin/vaadin-checkbox';
import '@vaadin/vaadin-radio-button/vaadin-radio-button';
import '@vaadin/vaadin-radio-button/vaadin-radio-group';
import '../../components/buttons-bar.js'


// @ts-ignore
@customElement('user-authorities')
export class UserAuthorities extends LitElement {
    createRenderRoot() {
        // Do not use a shadow root
        return this;
    }

    render() {
        return html`

            <style include="shared-styles">
                :host {
                    display: flex;
                    flex-direction: column;
                    flex: auto;
                }

                .meta-row {
                    display: flex;
                    justify-content: space-between;
                    padding-bottom: var(--lumo-space-s);
                }

                .dim {
                    color: var(--lumo-secondary-text-color);
                    text-align: right;
                    white-space: nowrap;
                    line-height: 2.5em;
                }

                .status {
                    width: 10em;
                }
            </style>
            <vaadin-combo-box id="clients" label="Cliente" clear-button-visible></vaadin-combo-box>
            <vaadin-board>
                <!--                <vaadin-horizontal-layout style="width: 100%">-->
                <!--                        <vaadin-checkbox-group label="DashBoard" theme="vertical" style="width: 50%">-->
                <!--                            <vaadin-checkbox value="0" checked disabled>Consultar</vaadin-checkbox>-->
                <!--                            <vaadin-checkbox value="1" disabled>Crear</vaadin-checkbox>-->
                <!--                            <vaadin-checkbox value="2" disabled>Editar</vaadin-checkbox>-->
                <!--                            <vaadin-checkbox value="3" disabled>Borrar</vaadin-checkbox>-->
                <!--                        </vaadin-checkbox-group>-->
                <!--                        <vaadin-checkbox-group label="Trafico por Cliente" theme="vertical" style="width: 50%">-->
                <!--                            <vaadin-checkbox value="0">Consultar</vaadin-checkbox>-->
                <!--                            <vaadin-checkbox value="1">Crear</vaadin-checkbox>-->
                <!--                            <vaadin-checkbox value="2">Editar</vaadin-checkbox>-->
                <!--                            <vaadin-checkbox value="3">Borrar</vaadin-checkbox>-->
                <!--                        </vaadin-checkbox-group>-->
                <!--                        <vaadin-checkbox-group label="Evolucion por Cliente" theme="vertical" style="width: 50%">-->
                <!--                            <vaadin-checkbox value="0">Consultar</vaadin-checkbox>-->
                <!--                            <vaadin-checkbox value="1">Crear</vaadin-checkbox>-->
                <!--                            <vaadin-checkbox value="2">Editar</vaadin-checkbox>-->
                <!--                            <vaadin-checkbox value="3">Borrar</vaadin-checkbox>-->
                <!--                        </vaadin-checkbox-group>-->
                <!--                        <vaadin-checkbox-group label="Evolucion Operadora" theme="vertical" style="width: 50%">-->
                <!--                            <vaadin-checkbox value="0">Consultar</vaadin-checkbox>-->
                <!--                            <vaadin-checkbox value="1">Crear</vaadin-checkbox>-->
                <!--                            <vaadin-checkbox value="2">Editar</vaadin-checkbox>-->
                <!--                            <vaadin-checkbox value="3">Borrar</vaadin-checkbox>-->
                <!--                        </vaadin-checkbox-group>-->
                <!--                    </vaadin-horizontal-layout>-->
                <!--                    <vaadin-row>-->
                <!--                        <vaadin-horizontal-layout style="width: 100%">-->
                <!--                            <vaadin-checkbox-group label="Busqueda de Mensaje" theme="vertical" style="width: 50%">-->
                <!--                                <vaadin-checkbox value="0">Consultar</vaadin-checkbox>-->
                <!--                                <vaadin-checkbox value="1">Crear</vaadin-checkbox>-->
                <!--                                <vaadin-checkbox value="2">Editar</vaadin-checkbox>-->
                <!--                                <vaadin-checkbox value="3">Borrar</vaadin-checkbox>-->
                <!--                            </vaadin-checkbox-group>-->
                <!--                            <vaadin-checkbox-group label="Crear Masivos" theme="vertical" style="width: 50%">-->
                <!--                                <vaadin-checkbox value="0">Consultar</vaadin-checkbox>-->
                <!--                                <vaadin-checkbox value="1">Crear</vaadin-checkbox>-->
                <!--                                <vaadin-checkbox value="2">Editar</vaadin-checkbox>-->
                <!--                                <vaadin-checkbox value="3">Borrar</vaadin-checkbox>-->
                <!--                            </vaadin-checkbox-group>-->
                <!--                            <vaadin-checkbox-group label="Programación Masivos" theme="vertical" style="width: 50%">-->
                <!--                                <vaadin-checkbox value="0">Consultar</vaadin-checkbox>-->
                <!--                                <vaadin-checkbox value="1">Crear</vaadin-checkbox>-->
                <!--                                <vaadin-checkbox value="2">Editar</vaadin-checkbox>-->
                <!--                                <vaadin-checkbox value="3">Borrar</vaadin-checkbox>-->
                <!--                            </vaadin-checkbox-group>-->
                <!--                            <vaadin-checkbox-group label="Auditoria" theme="vertical" style="width: 50%">-->
                <!--                                <vaadin-checkbox value="0">Consultar</vaadin-checkbox>-->
                <!--                                <vaadin-checkbox value="1">Crear</vaadin-checkbox>-->
                <!--                                <vaadin-checkbox value="2">Editar</vaadin-checkbox>-->
                <!--                                <vaadin-checkbox value="3">Borrar</vaadin-checkbox>-->
                <!--                            </vaadin-checkbox-group>-->
                <!--                        </vaadin-horizontal-layout>-->
                <!--                    </vaadin-row>-->
                <!--                    <vaadin-row>-->
                <!--                        <vaadin-horizontal-layout style="width: 100%">-->
                <!--                            <vaadin-checkbox-group label="Usuarios" theme="vertical" style="width: 25%">-->
                <!--                                <vaadin-checkbox value="0">Consultar</vaadin-checkbox>-->
                <!--                                <vaadin-checkbox value="1">Crear</vaadin-checkbox>-->
                <!--                                <vaadin-checkbox value="2">Editar</vaadin-checkbox>-->
                <!--                                <vaadin-checkbox value="3">Borrar</vaadin-checkbox>-->
                <!--                            </vaadin-checkbox-group>-->
                <!--                            <vaadin-checkbox-group label="Permisos" theme="vertical" style="width: 25%">-->
                <!--                                <vaadin-checkbox value="0">Consultar</vaadin-checkbox>-->
                <!--                                <vaadin-checkbox value="1">Crear</vaadin-checkbox>-->
                <!--                                <vaadin-checkbox value="2">Editar</vaadin-checkbox>-->
                <!--                                <vaadin-checkbox value="3">Borrar</vaadin-checkbox>-->
                <!--                            </vaadin-checkbox-group>-->
                <!--                        </vaadin-horizontal-layout>-->
                <!--                    </vaadin-row>-->
                <vaadin-row>
                    <vaadin-horizontal-layout style="width: 100%">
                        <vaadin-checkbox-group label="Usuarios" theme="vertical" style="width: 50%">
                            <vaadin-checkbox value="0">Dashboard</vaadin-checkbox>
                            <vaadin-checkbox value="1">Trafico por Cliente</vaadin-checkbox>
                            <vaadin-checkbox value="2">Evolucion Operadora</vaadin-checkbox>
                            <vaadin-checkbox value="3">Borrar</vaadin-checkbox>
                        </vaadin-checkbox-group>
                    </vaadin-horizontal-layout>
                </vaadin-row>
            </vaadin-board>

            <buttons-bar id="footer" no-scroll\\$="[[noScroll]]">
                <vaadin-button slot="left" id="cancel">Cancelar</vaadin-button>
                <vaadin-button slot="right" id="review" theme="primary">
                    Confirmar programacion
                    <iron-icon icon="vaadin:arrow-right" slot="suffix"></iron-icon>
                </vaadin-button>
            </buttons-bar>

        `;
    }
}