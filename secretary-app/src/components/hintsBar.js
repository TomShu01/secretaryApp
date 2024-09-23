import Chip from '@mui/material/Chip';
import KeyboardIcon from '@mui/icons-material/Keyboard';

// takes in hints, a dictionary, containing key-value pairs (action, bind) where the key is name of control action and the value is the keybinds
const HintsBar = ({ hints }) => {
    return (
        <div style={{diplay: 'block'}}>
            {hints.map((hint, index) =>
            <Chip
            icon={<KeyboardIcon style={{ color: '#fff' }} />}
            label={hint.bind}
            style={{
                margin: '5px',
                backgroundColor: 'rgba(255, 255, 255, 0.1)',
                color: '#fff',
            }}
            />)}   
        </div>
    )
}

export default HintsBar