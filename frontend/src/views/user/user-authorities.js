import {html, LitElement} from 'lit-element';
import '@vaadin/vaadin-text-field';
import '@vaadin/vaadin-button';
import '@vaadin/vaadin-checkbox';
import '@vaadin/vaadin-radio-button/vaadin-radio-button';
import '@vaadin/vaadin-radio-button/vaadin-radio-group';
import '../../components/buttons-bar.js'


class UserAuthorities extends LitElement {
    createRenderRoot() {
        // Do not use a shadow root
        return this;
    }

    render() {
        return html`

            <style include="shared-styles">
                :host {
                    width: 100%;
                    -webkit-overflow-scrolling: touch;
                    overflow: auto;
                }

                .vaadin-board-cell {
                    padding: var(--lumo-space-s);
                }

                *::-ms-backdrop,
                .vaadin-board-cell {
                    padding: 0;
                }

                vaadin-board-row.custom-board-row {
                    --vaadin-board-width-medium: 1024px;
                    --vaadin-board-width-small: 1024px;
                }

            </style>
            <vaadin-board>
                <vaadin-row>
                    <div id="firstline" class="vaadin-board-cell">
                        <vaadin-combo-box id="clients" label="Usuarios" clear-button-visible></vaadin-combo-box>
                        <vaadin-horizontal-layout>
                            <vaadin-checkbox-group id="checkbox1" label="Consulta" theme="vertical"
                                                   style="width: 33%">
                            </vaadin-checkbox-group>
                            <vaadin-checkbox-group id="checkbox2" label="Auditoria" theme="vertical"
                                                   style="width: 33%">
                            </vaadin-checkbox-group>
                            <vaadin-checkbox-group id="checkbox3" label="Administrador" theme="vertical"
                                                   style="width: 33%">
                            </vaadin-checkbox-group>
                        </vaadin-horizontal-layout>
                        <hr/>
                        <vaadin-horizontal-layout style="width: 25%;">
                            <vaadin-button id="select-all" theme="small" style="margin-inline-end: auto;">Seleccionar
                                todos
                                <iron-icon icon="vaadin:check-square" slot="prefix"></iron-icon>
                            </vaadin-button>
                            <vaadin-button id="remove-all" theme="small">Quitar todos
                                <iron-icon icon="vaadin:check-square-o" slot="prefix"></iron-icon>
                            </vaadin-button>
                        </vaadin-horizontal-layout>
                    </div>
                    <div id="secondline" class="vaadin-board-cell"></div>
                </vaadin-row>
                <buttons-bar id="footer" class="vaadin-board-cell">
                    <vaadin-button slot="right" id="save" theme="primary">
                        Guardar
                        <iron-icon icon="vaadin:arrow-right" slot="suffix"></iron-icon>
                    </vaadin-button>
                </buttons-bar>
            </vaadin-board>



        `;
    }
}

customElements.define('user-authorities', UserAuthorities)