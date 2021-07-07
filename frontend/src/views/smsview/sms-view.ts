import {customElement, html, LitElement} from 'lit-element';

@customElement('sms-view')
export class SmsView extends LitElement {

    createRenderRoot() {
        // Do not use a shadow root
        return this;
    }

    render() {
        return html`
            <div>
                <vaadin-date-picker id="dateOne" label="Desde" ></vaadin-date-picker>
                <vaadin-date-picker id="dateTwo" label="Hasta" ></vaadin-date-picker>
                <vaadin-text-field id="textPhoneNumer" label="Num. Telefono" ></vaadin-text-field>
                <multiselect-combo-box id="comboCarrier" label="Operadoras" ></multiselect-combo-box>
                <multiselect-combo-box id="multi_systemIds" label="Credenciales" ></multiselect-combo-box>
                <multiselect-combo-box id="multi_messagetype" label="Mensajes" ></multiselect-combo-box>
                <vaadin-button id="searchButton" label="Buscar"></vaadin-button>
            </div>
        `;
    }
}